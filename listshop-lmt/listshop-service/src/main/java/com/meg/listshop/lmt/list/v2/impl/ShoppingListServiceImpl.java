/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.v2.impl;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.ItemOperationType;
import com.meg.listshop.lmt.api.model.ListOperationType;
import com.meg.listshop.lmt.api.model.v2.MergeRequest;
import com.meg.listshop.lmt.api.model.v2.MergeResult;
import com.meg.listshop.lmt.api.model.v2.SourceReferenceType;
import com.meg.listshop.lmt.api.model.v2.V2ModelMapper;
import com.meg.listshop.lmt.conversion.BasicAmount;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.*;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.BaseShoppingListService;
import com.meg.listshop.lmt.list.LegacyShoppingListService;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.list.v2.ShoppingListService;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.lmt.service.layout.LayoutService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional(rollbackFor = ItemProcessingException.class)
public class ShoppingListServiceImpl extends BaseShoppingListService implements ShoppingListService {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingListServiceImpl.class);
    private final LayoutService listLayoutService;
    private final ListItemDetailRepository itemDetailRepository;

    @Autowired
    public ShoppingListServiceImpl(TagService tagService,
                                   DishService dishService,
                                   ShoppingListRepository shoppingListRepository,
                                   @Qualifier("V2LayoutService") LayoutService listLayoutService,
                                   MealPlanService mealPlanService,
                                   ItemRepository itemRepository,
                                   ListItemDetailRepository itemDetailRepository,
                                   ItemChangeRepository itemChangeRepository,
                                   ListTagStatisticService listTagStatisticService,
                                   ListItemStateMachine listItemStateMachine) {
        super(tagService, dishService, shoppingListRepository,
                mealPlanService, itemRepository, itemChangeRepository, listTagStatisticService, listItemStateMachine);
        this.listLayoutService = listLayoutService;
        this.itemDetailRepository = itemDetailRepository;
    }

    @Override
    public MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest) throws ItemProcessingException {
        Long listToMergeId = mergeRequest.getListId();
        if (listToMergeId == null) {
            // oops - no list id
            throw new ObjectNotFoundException(String.format("List to merge has empty listId for user [%s]", userId));
        }
        // get list to merge
        ShoppingListEntity list = getListForUserById(userId, listToMergeId);

        if (list == null) {
            // oops - list isn't here any more to merge
            throw new ObjectNotFoundException(String.format("List to merge [%s] not found for user [%s]", listToMergeId, userId));
        }

        if (!requiresMerge(mergeRequest)) {
            // this list doesn't need to be merged
            logger.info("Skipping merge for list [{}].", listToMergeId);
            return new MergeResult();
        }

        logger.info("Offline changes found, proceeding with merge for list [{}].", listToMergeId);
        LocalDate serverLastUpdate = DateUtils.asLocalDate(list.getLastUpdate());

        // get all items for list as lookup hash by itemid
        Map<Long, ListItemEntity> listItemLookup = list.getItems().stream()
                .collect(Collectors.toMap(
                        ListItemEntity::getId,
                        Function.identity()));
        // convert passed mergeitems into mergeitemdtos (with a healthy equals)
        List<ItemMergeDTO> mergeItems = V2ModelMapper.toMergeItemDtoList(mergeRequest.getMergeItems());
        // iterate through mergeitemdtos
        Iterator<ItemMergeDTO> mergeItemIterator = mergeItems.iterator();
        // on the way, save removedListItems, changedListItems, newListItems
        List<ItemMergeDTO> removedFromServer = new ArrayList<>();
        List<ListItemEntity> removedListItems = new ArrayList<>();
        List<ListItemEntity> changedListItems = new ArrayList<>();
        List<ItemMergeDTO> addedListItems = new ArrayList<>();
        List<Long> addedAdditionalChanges = new ArrayList<>();
        while (mergeItemIterator.hasNext()) {
            ItemMergeDTO mergeItem = mergeItemIterator.next();
            Long itemId = getItemIdAsLong(mergeItem.getItemId());
            ListItemEntity serverItem = getMatchingItem(itemId, listItemLookup);

            if (itemId == null) {
                // no item from client - added on client side
                addedListItems.add(mergeItem);
                if (mergeItem.getCrossedOff() != null) {
                    addedAdditionalChanges.add(Long.valueOf(mergeItem.getTagId()));
                }
                continue;
            } else if (serverItem == null) {
                // no item matching merge item found - possibly deleted physically on server side
                removedFromServer.add(mergeItem);
                continue;
            }


            if (!clientItemMoreFresh(mergeItem, serverItem)) {
                continue;
            }

            if (clientItemRemoved(mergeItem)) {
                // merge changes
                ListItemEntity removed = mergeClientChangesIntoItem(mergeItem, serverItem);
                removedListItems.add(removed);
            } else {
                // merge changes
                ListItemEntity change = mergeClientChangesIntoItem(mergeItem, serverItem);
                changedListItems.add(change);
            }
        }

        // check that items removed from server have not been re-added from client
        List<ItemMergeDTO> toRemove = removedFromServer.stream()
                .filter(client -> dateIsAfter(client.getAddedOn(), serverLastUpdate))
                .toList();

        if (allListsEmpty(removedFromServer, removedListItems, changedListItems, addedListItems)) {
            return new MergeResult();
        }

        // remove items using tagId
        List<Long> removeIds = toRemove.stream()
                .map(ItemMergeDTO::getTagId)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        removeIds.addAll(removedListItems.stream()
                .map(i -> i.getTag().getId())
                .collect(Collectors.toList()));
        if (!removeIds.isEmpty()) {
            doMoveRemoveItemOperations(list, userId, list.getId(), ItemOperationType.Remove, removeIds, null);
            listTagStatisticService.processStatistics(userId, new ArrayList<>(), removeIds, ListOperationType.TAG_REMOVE);
        }

        // save changed items
        if (!changedListItems.isEmpty()) {
            itemRepository.saveAll(changedListItems);
        }

        // create new list items
        if (!addedListItems.isEmpty()) {
            List<ListItemEntity> addedItems = addNewItemsToList(list, addedListItems, userId);
            // now that the lists are added, we need to make sure that they align with the merge items
            // (specifically the crossed off)
            if (!addedAdditionalChanges.isEmpty()) {
                Map<String, ItemMergeDTO> mergeItemMap = mergeItems.stream()
                        .collect(Collectors.toMap(ItemMergeDTO::getTagId, Function.identity()));
                addedItems.stream()
                        .filter(i -> addedAdditionalChanges.contains(i.getTag().getId()))
                        .forEach(i -> {
                            ItemMergeDTO mergeItem = mergeItemMap.get(String.valueOf(i.getTag().getId()));
                            Date crossedOff = mergeItem != null ? mergeItem.getCrossedOff() : null;
                            i.setCrossedOff(crossedOff);
                        });
            }
            listTagStatisticService.processStatistics(userId, addedItems, null, ListOperationType.TAG_ADD);
        }

        // save list updated
        list.setLastUpdate(new Date());
        shoppingListRepository.save(list);

        return new MergeResult();

    }

    private boolean allListsEmpty(List<ItemMergeDTO> removedFromServer, List<ListItemEntity> removedListItems,
                                  List<ListItemEntity> changedListItems, List<ItemMergeDTO> addedListItems) {
        return List.of(removedFromServer, removedListItems, changedListItems, addedListItems)
                .stream()
                .allMatch(l -> l == null || l.isEmpty());
    }

    private boolean dateIsAfter(Date date, LocalDate isAfterThisDate) {
        return DateUtils.asLocalDate(date).isAfter(isAfterThisDate);
    }

    private ListItemEntity mergeClientChangesIntoItem(ItemMergeDTO mergeItem, ListItemEntity serverItem) {

        serverItem.setAddedOn(mergeItem.getAddedOn());
        serverItem.setCrossedOff(mergeItem.getCrossedOff());
        serverItem.setRemovedOn(mergeItem.getRemoved());
        serverItem.setUpdatedOn(new Date());
        serverItem.setLastChanged(new Date());
        return serverItem;
    }

    private boolean clientItemRemoved(ItemMergeDTO mergeItem) {
        return mergeItem.getRemoved() != null;
    }

    private boolean clientItemMoreFresh(ItemMergeDTO mergeItem, ListItemEntity serverItem) {
        if (mergeItem.getLastChanged() == null || serverItem == null || serverItem.getLastChanged() == null) {
            return false;
        }
        return mergeItem.getLastChanged().after(serverItem.getLastChanged());
    }

    private ListItemEntity getMatchingItem(Long itemId, Map<Long, ListItemEntity> listItemLookup) {
        if (itemId == null) {
            return null;
        }
        if (listItemLookup.containsKey(itemId)) {
            return listItemLookup.get(itemId);
        }
        return null;
    }

    private Long getItemIdAsLong(String itemId) {
        if (itemId == null) {
            return null;
        }
        return Long.valueOf(itemId);
    }

    public MergeResult legacyMergeFromClient(Long userId, MergeRequest mergeRequest) throws ItemProcessingException {
        Long listToMergeId = mergeRequest.getListId();
        if (listToMergeId == null) {
            // oops - no list id
            throw new ObjectNotFoundException(String.format("List to merge has empty listId for user [%s]", userId));
        }
        // get list to merge
        ShoppingListEntity list = getListForUserById(userId, listToMergeId);

        if (list == null) {
            // oops - list isn't here any more to merge
            throw new ObjectNotFoundException(String.format("List to merge [%s] not found for user [%s]", listToMergeId, userId));
        }

        if (!requiresMerge(mergeRequest)) {
            // this list doesn't need to be merged
            logger.info("Skipping merge for list [{}].", listToMergeId);
            return new MergeResult();
        }

        logger.info("Offline changes found, proceeding with merge for list [{}].", listToMergeId);
        LocalDate mergedLastUpdate = DateUtils.asLocalDate(mergeRequest.getLastChanged());
        LocalDate serverLastUpdate = DateUtils.asLocalDate(list.getLastUpdate());

        // get all items for list as lookup hash by itemid
        Map<Long, ListItemEntity> listItemLookup = list.getItems().stream()
                .collect(Collectors.toMap(
                        ListItemEntity::getId,
                        Function.identity()));
        // convert passed mergeitems into mergeitemdtos (with a healthy equals)
        List<ItemMergeDTO> mergeItems = V2ModelMapper.toMergeItemDtoList(mergeRequest.getMergeItems());
        // iterate through mergeitemdtos
        Iterator<ItemMergeDTO> mergeItemIterator = mergeItems.iterator();
        // on the way, save removedListItems, changedListItems, newListItems
        List<ItemMergeDTO> removedFromServer = new ArrayList<>();
        List<ListItemEntity> removedListItems = new ArrayList<>();
        List<ListItemEntity> changedListItems = new ArrayList<>();
        List<ItemMergeDTO> newListItems = new ArrayList<>();
        while (mergeItemIterator.hasNext()) {
            //MM thoughts -
            // what we need is a way to determine which item is stale on the item level
            // so - mergeitem would need to provide "lastChange" (possibly with offline flag)
            //    - listitem entity would need to provide "lastChange"
            //  and then, we only pay attention to items where the mergitem last change is after the listitem lastchange
            // this way, if we have a difference in removed on, or changed on - we know that we need to take the client version

            // for removedon - it's more tricky because we remove the item directly from the database. so it isn't there to compare
            // so, if we have a client item which doesn't exist in the list, we need to check -
            //    item last update > server list last change.  if so, add it.  if not, ignore it
            // not the best, since we're comparing list to list item - but it's the best we can do
            // will be untrustworthy if we have changes to the list on the web before merging removed.
            // but typically, the client won't be in a state where there are lots of changes which aren't merged.
            // typically, this happens only in offline mode, and a client app will stay open and be able to merge when going back online.
            // this edge case is what would potentially be handled with the conflicts -
            // item not in server list, but changes from client - pop these in conflicts and let the user decide


            ItemMergeDTO mergeItem = mergeItemIterator.next();
            if (mergeItem.getItemId() == null) {
                newListItems.add(mergeItem);
                continue;
            }
            ListItemEntity listItem = listItemLookup.get(Long.valueOf(mergeItem.getItemId()));

            if (listItem == null) {
                // item id no longer exists on the server side
                removedFromServer.add(mergeItem);
            } else if (shouldUseClientChange(mergeItem.getCrossedOff(), listItem.getCrossedOff(), new Date())) {

            }

            if (listItem == null) {
                if (DateUtils.asLocalDate(mergeItem.getUpdated()).isAfter(serverLastUpdate)) {
                    // add item
                    newListItems.add(mergeItem);
                }
            } else if (mergeItem.getRemoved() != null) {
                if (DateUtils.asLocalDate(mergeItem.getRemoved()).isAfter(serverLastUpdate)) {
                    // remove item
                    removedListItems.add(listItem);
                }
            } else if (mergeItem.getUpdated() != null) {
                if (DateUtils.asLocalDate(mergeItem.getUpdated()).isAfter(serverLastUpdate)) {
                    // change item
                    listItem.setCrossedOff(mergeItem.getCrossedOff());
                    listItem.setUpdatedOn(mergeItem.getUpdated());
                    listItem.setLastChanged(new Date());
                    listItem.setRemovedOn(null);
                    listItem.setAddedOn(mergeItem.getAddedOn());
                    changedListItems.add(listItem);
                }
            }
        }

        if (newListItems.isEmpty() && removedListItems.isEmpty() && changedListItems.isEmpty()) {
            return new MergeResult();
        }

        // save changed items
        if (!changedListItems.isEmpty()) {
            itemRepository.saveAll(changedListItems);
        }

        // create new list items
        if (!newListItems.isEmpty()) {
            List<ListItemEntity> addedItems = addNewItemsToList(list, newListItems, userId);
            listTagStatisticService.processStatistics(userId, addedItems, null, ListOperationType.TAG_ADD);
        }

        // remove items to remove
        if (!removedListItems.isEmpty()) {
            List<Long> removeIds = removedListItems.stream().map(i -> i.getTag().getId()).collect(Collectors.toUnmodifiableList());
            doMoveRemoveItemOperations(list, userId, null, ItemOperationType.Remove, removeIds, null);
            listTagStatisticService.processStatistics(userId, new ArrayList<>(), removeIds, ListOperationType.TAG_REMOVE);
        }

        // save list updated
        list.setLastUpdate(new Date());
        shoppingListRepository.save(list);

        return new MergeResult();

/*
        // create MergeCollector from list
        MergeItemCollector mergeCollector = new MergeItemCollector(list.getId(), list.getItems(), list.getLastUpdate());
        checkReplaceTagsInCollector(mergeCollector);

        // prepare items from client
        List<ListItemEntity> mergeItems = convertClientItemsToItemEntities(userId, mergeRequest);

        // merge from client
        logger.debug("Preparing to merge list [{}].", list.getId());
        mergeCollector.addMergeItems(mergeItems);

        // update after merge
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Merge)
                .withStatisticCountType(StatisticCountType.Single)
                .build();
        legacySaveListChanges(list, mergeCollector, context);

        logger.info("Merge complete for list [{}}].", list.getId());
        return new MergeResult();
*/
    }

    private boolean shouldUseClientChange(Date clientDate, Date serverDate, Date server) {
        //MM returns false if dates are the same
        // returns true
        return false;
    }

    private boolean requiresMerge(MergeRequest mergeRequest) {
        // for older clients which aren't sending info - we keep the old behavior, which is to always merge
        if (mergeRequest.getLastOfflineChange() == null && mergeRequest.getLastSynced() == null) {
            return true;
        }
        if (mergeRequest.getLastOfflineChange() == null) {
            return false;
        }
        // last offline change more recent than last synced - we need to merge the offline changes
        return (mergeRequest.getLastSynced() != null &&
                mergeRequest.getLastOfflineChange().after(mergeRequest.getLastSynced()));
    }

    public List<CategoryDTO> retrieveListCategories(Long id) {
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findById(id).orElse(null);

        if (shoppingListEntity == null) {
            return new ArrayList<>();
        }
        Long userLayoutId = determineUserLayout(shoppingListEntity.getUserId(), shoppingListEntity.getListLayoutId());

        // find frequently crossed off
        List<Long> frequentTagIds = listTagStatisticService.findFrequentIdsForList(shoppingListEntity.getId(), shoppingListEntity.getUserId());
        // gather mapping information
        Map<String, CategoryDTO> categoryMap = retrieveCategoriesForListAndLayout(userLayoutId, shoppingListEntity.getId());
        Map<Long, String> tagToCategory = retrieveItemToCategoryMapping(shoppingListEntity.getId(), userLayoutId).stream()
                .collect(Collectors.toMap(ItemToCategoryDTO::tagId, ItemToCategoryDTO::categoryName));

        // then we need to retrieve filled ListItems for list - just filler here - may need more "energetic" in terms of entity graph
        List<ListItemEntity> listItems = itemRepository.findFilledObjectsByListId(shoppingListEntity.getId());
        for (ListItemEntity listItem : listItems) {
            if (listItem.getRemovedOn() != null) {
                continue;
            }
            Long tagId = listItem.getTag().getId();
            // get source list for listItem (looking at details, frequent)
            Set<String> sourceStrings = extractSourceDTO(listItem);
            if (frequentTagIds.contains(tagId)) {
                sourceStrings.add(LegacyShoppingListService.FREQUENT);
            }
            // map to ListItemDTO
            ListItemDTO listItemDTO = new ListItemDTO(listItem, sourceStrings);
            // get category name from category map
            String categoryName = tagToCategory.get(tagId);
            // add ListItemDTO to items in category
            if (categoryName == null) {
                continue;
            }
            categoryMap.get(categoryName).getItems().add(listItemDTO);
        }
        // sort items in category by lower tag name
        categoryMap.values().forEach(category -> {
            category.getItems().sort(Comparator.comparing(item -> item.getTag().getName().toLowerCase()));
        });
        // sort categories by display order
        List<CategoryDTO> result = new ArrayList<>(categoryMap.values());
        result.sort(Comparator.comparing(CategoryDTO::getDisplayOrder));
        // return list of categories
        return result;
    }

    private Set<String> extractSourceDTO(ListItemEntity listItem) {
        List<SourceDTO> sources = new ArrayList<>();
        listItem.getDetails().stream()
                .filter(detail -> detail.getLinkedDishId() != null)
                .forEach(detail -> sources.add(new SourceDTO(detail.getLinkedDishId(), null, SourceReferenceType.DISH.name())));
        listItem.getDetails().stream()
                .filter(detail -> detail.getLinkedListId() != null && !detail.getLinkedListId().equals(listItem.getListId()))
                .forEach(detail -> sources.add(new SourceDTO(detail.getLinkedListId(), null, SourceReferenceType.LIST.name())));
        return sources.stream()
                .map(s -> s.getReferenceType().name() + s.getReferenceId())
                .collect(Collectors.toSet());
    }

    private List<ItemToCategoryDTO> retrieveItemToCategoryMapping(Long listId, Long userLayoutId) {
        List<ItemToCategoryDTO> mappings = new ArrayList<>();
        mappings.addAll(itemRepository.getUserItemToCategoryMapping(userLayoutId, listId));
        Set<Long> tagIds = mappings.stream().map(ItemToCategoryDTO::tagId).collect(Collectors.toSet());
        itemRepository.getStandardItemToCategoryMapping(listId).stream()
                .filter(mapping -> !tagIds.contains(mapping.tagId()))
                .forEach(mapping -> mappings.add(mapping));
        return mappings;
    }

    private Map<String, CategoryDTO> retrieveCategoriesForListAndLayout(Long userLayoutId, Long listId) {
        // from repository, get distinct list_categories for all items in list
        Map<String, CategoryDTO> categoryMap = getUserCategoriesForList(userLayoutId, listId);
        List<ListLayoutCategoryEntity> standardCategories = listLayoutService.getStandardCategoriesForList(listId);
        standardCategories.stream()
                .filter(category -> !categoryMap.containsKey(toTrimmedLower(category.getName())))
                .forEach(category -> categoryMap.put(toTrimmedLower(category.getName()),
                        new CategoryDTO(category.getId(), category.getName(), category.getDisplayOrder())));
        return categoryMap;
    }

    private String toTrimmedLower(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase();
    }

    private Map<String, CategoryDTO> getUserCategoriesForList(Long userLayoutId, Long listId) {
        List<ListLayoutCategoryEntity> layoutCategories = listLayoutService.getUserCategoriesForList(userLayoutId, listId);
        if (layoutCategories == null || layoutCategories.isEmpty()) {
            return new HashMap<>();
        }
        return layoutCategories.stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getDisplayOrder()))
                .collect(Collectors.toMap(CategoryDTO::getComparisonName, Function.identity()));
    }

    @Override
    public List<SourceDTO> retrieveListSources(Long id) {
        List<SourceDTO> sources = itemRepository.findDishSourcesForList(id);
        sources.addAll(itemRepository.findListSourcesForList(id));
        return sources;
    }

    @Override
    public ShoppingListDTO getListDTOForUser(Long userId, Long listId) {
        return shoppingListRepository.findDTOByListIdAndUserId(listId, userId);
    }

    @Override
    public Map<Long, String> retrieveUnitMapping(Long listId) {
        Set<UnitEntity> units = new HashSet<>(itemRepository.findUnitsForItems(listId));
        units.addAll(itemRepository.findUnitsForItemDetails(listId));

        return units.stream().collect(Collectors.toMap(UnitEntity::getId, UnitEntity::getName));
    }


    private List<ListItemEntity> addNewItemsToList(ShoppingListEntity list, List<ItemMergeDTO> newListItems, Long userId) throws ItemProcessingException {
        Long listId = list.getId();

        List<ListItemEntity> results = new ArrayList<>();
        for (ItemMergeDTO mergeItem : newListItems) {
            if (mergeItem.getRemoved() != null) {
                continue;
            }
            Long tagId = Long.valueOf(mergeItem.getTagId());
            SimpleListItemDTO addItem = SimpleListItemDTO.from(tagId, listId);
            ListItemEntity result = doAddItemToList(tagId, addItem, null, listId, userId);
            results.add(result);

        }

        list.getItems().addAll(results);
        return results;
    }

    @Override
    public void addItemToList(Long userId, Long listId, SimpleListItemDTO itemDTO) throws ItemProcessingException {
        Long tagId = itemDTO.getTagId();
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ListItemEntity item = shoppingListEntity.getItems().stream()
                .filter(l -> l.getTag().getId().equals(tagId))
                .findFirst()
                .orElse(null);
        boolean isNew = item == null;

        ListItemEntity result = doAddItemToList(tagId, itemDTO, item, listId, userId);

        if (isNew) {
            shoppingListEntity.getItems().add(result);
        }

        saveListChanges(shoppingListEntity,
                Collections.singletonList(result),
                ListOperationType.TAG_ADD);
    }

    private ListItemEntity doAddItemToList(Long tagId, SimpleListItemDTO itemDTO, ListItemEntity item, Long listId, Long userId) throws ItemProcessingException {
        TagEntity tag = tagService.getTagById(tagId);
        BasicAmount amount = pullAmountFromSimpleItem(itemDTO, tag);
        ItemStateContext itemStateContext = new ItemStateContext(item, listId);
        itemStateContext.setTag(tag);
        itemStateContext.setTagAmount(amount);

        return listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, userId);

    }


    private BasicAmount pullAmountFromSimpleItem(SimpleListItemDTO item, TagEntity tag) {
        if (item == null || item.getQuantity() == null || item.getQuantity() == 0.0) {
            return null;
        }
        return new BasicAmount(item.getQuantity(), item.getMarker(), item.getUnitSize(), item.getUnitId(), tag);
    }

    public ShoppingListDTO getStarterList(Long userId) {

        List<ShoppingListDTO> foundLists = shoppingListRepository.findDTOByUserIdAndIsStarterListTrue(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    public ShoppingListDTO getMostRecentList(Long userId) {

        List<ShoppingListDTO> foundLists = shoppingListRepository.findByUserId(userId);
        if (!foundLists.isEmpty()) {
            Long firstId = foundLists.get(0).getListId();
            Long listId = foundLists.stream()
                    .filter(list -> list.getLastUpdate() != null)
                    .map(ShoppingListDTO::getListId)
                    .findFirst().orElse(firstId);

            return shoppingListRepository.findDTOById(listId);
        }
        return null;
    }

    @Override
    public void deleteList(Long userId, Long listId) throws ItemProcessingException, BadParameterException {
        List<ShoppingListDTO> allLists = shoppingListRepository.findByUserId(userId);
        if (allLists == null || allLists.isEmpty()) {
            throw new ActionInvalidException(String.format("No lists found for user [%s]", userId));
        }
        if (allLists.size() < 2) {
            throw new ActionInvalidException(String.format("Can't delete the last list for user [%s]", userId));
        }
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        if (shoppingList == null) {
            throw new ObjectNotFoundException(String.format("Can't find list [%s] for userName [%s] to delete.", listId, userId));
        }

        // remove links to other lists
        handleLinkedListItems(userId, listId);

        // delete the list
        shoppingListRepository.delete(shoppingList);
    }

    private void handleLinkedListItems(Long userId, Long listId) throws ItemProcessingException {
        // get all items linked to the list
        List<Long> linkedItemsIds = itemRepository.findItemIdsForLinkedList(listId);
        List<ListItemEntity> linkedItems = itemRepository.getFilledItemsByItemIds(linkedItemsIds);
        // for each item, remove link, and merge detail
        for (ListItemEntity item : linkedItems) {
            if (listId.equals(item.getListId())) {
                continue;
            }
            ItemStateContext context = new ItemStateContext(item, null);
            context.setRemoveListLinkId(listId);
            context.setTag(item.getTag());

            listItemStateMachine.handleEvent(ListItemEvent.REMOVE_LINK, context, userId);

        }
    }

    public ShoppingListEntity updateList(Long userId, Long listId, ShoppingListDTO updateFrom) {
        // get list
        Optional<ShoppingListEntity> byUserNameAndId = shoppingListRepository.findByListIdAndUserId(listId, userId);
        if (byUserNameAndId.isEmpty()) {
            throw new ObjectNotFoundException(String.format("List [%s] not found for user [%s] in updateList", listId, userId));
        }
        ShoppingListEntity copyTo = byUserNameAndId.get();

        // check starter list change
        boolean starterListChanged = updateFrom.isStarterList() && !copyTo.getIsStarterList();
        if (starterListChanged) {
            clearStarterList(userId);
        }
        // copy fields from updateFrom
        copyTo.setIsStarterList(updateFrom.isStarterList());
        copyTo.setName(updateFrom.getName());

        // save changed list
        copyTo.setLastUpdate(new Date());
        return shoppingListRepository.save(copyTo);
    }

    private void clearStarterList(Long userId) {
        shoppingListRepository.clearStarterListForUser(userId);
    }

    private Long determineUserLayout(Long userId, Long listLayoutId) {
        Optional<ListLayoutEntity> layout;
        if (listLayoutId == null) {
            layout = Optional.ofNullable(listLayoutService.getDefaultUserLayout(userId));
        } else {
            layout = Optional.ofNullable(listLayoutService.getUserListLayout(userId, listLayoutId));
        }

        return layout.map(ListLayoutEntity::getId)
                .orElse(null);
    }


    protected Long getDefaultListLayoutId(Long userId) {
        ListLayoutEntity listLayout = listLayoutService.getDefaultUserLayout(userId);
        return listLayout != null ? listLayout.getId() : null;
    }

}
