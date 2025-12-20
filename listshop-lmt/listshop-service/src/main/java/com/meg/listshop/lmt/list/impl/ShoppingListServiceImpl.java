package com.meg.listshop.lmt.list.impl;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.list.ShoppingListException;
import com.meg.listshop.lmt.list.ShoppingListService;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional(rollbackFor = ItemProcessingException.class)
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingListServiceImpl.class);

    private final TagService tagService;
    private final DishService dishService;
    private final ShoppingListRepository shoppingListRepository;
    private final LayoutService listLayoutService;
    private final MealPlanService mealPlanService;
    private final ItemRepository itemRepository;
    private final ListTagStatisticService listTagStatisticService;
    private final ListItemStateMachine listItemStateMachine;

    private final
    ItemChangeRepository itemChangeRepository;

    @Value("${service.shoppinglistservice.merge.items.deleted.after.days}")
    int mergeDeleteAfterDays = 6;

    @Value("${service.shoppinglistservice.default.list.name}")
    String defaultShoppingListName;

    @Autowired
    public ShoppingListServiceImpl(TagService tagService,
                                   DishService dishService,
                                   ShoppingListRepository shoppingListRepository,
                                   LayoutService listLayoutService,
                                   MealPlanService mealPlanService,
                                   ItemRepository itemRepository,
                                   ItemChangeRepository itemChangeRepository,
                                   ListTagStatisticService listTagStatisticService,
                                   ListItemStateMachine listItemStateMachine) {
        this.tagService = tagService;
        this.dishService = dishService;
        this.shoppingListRepository = shoppingListRepository;
        this.listLayoutService = listLayoutService;
        this.mealPlanService = mealPlanService;
        this.itemRepository = itemRepository;
        this.itemChangeRepository = itemChangeRepository;
        this.listTagStatisticService = listTagStatisticService;
        this.listItemStateMachine = listItemStateMachine;
    }


    @Override
    public List<ShoppingListEntity> getListsByUserId(Long userId) {
        return shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId);
    }

    @Override
    public ShoppingListEntity updateList(Long userId, Long listId, ShoppingListEntity updateFrom) {
        // get list
        Optional<ShoppingListEntity> byUserNameAndId = shoppingListRepository.findByListIdAndUserId(listId, userId);
        if (byUserNameAndId.isEmpty()) {
            throw new ObjectNotFoundException(String.format("List [%s] not found for user [%s] in updateList", listId, userId));
        }
        ShoppingListEntity copyTo = byUserNameAndId.get();

        // check starter list change
        boolean starterListChanged = updateFrom.getIsStarterList() && !copyTo.getIsStarterList();

        // copy fields from updateFrom
        copyTo.setIsStarterList(updateFrom.getIsStarterList());
        copyTo.setName(updateFrom.getName());

        if (starterListChanged) {
            ShoppingListEntity oldStarter = getStarterList(userId);
            if (oldStarter != null && !oldStarter.getId().equals(copyTo.getId())) {
                oldStarter.setIsStarterList(false);
                shoppingListRepository.save(oldStarter);
            }
        }

        // save changed list
        copyTo.setLastUpdate(new Date());
        return shoppingListRepository.save(copyTo);
    }

    @Override
    public void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) throws ItemProcessingException {
        logger.debug("Beginning performItemOperation with sourceListId [{}], destinationListId[{}],  tagIds [{}] and itemOperationType [{}]", sourceListId, destinationListId, tagIds, operationType);
        // get source list
        ShoppingListEntity sourceList = getListForUserById(userId, sourceListId);

        if (sourceList == null) {
            return;
        }
        switch (operationType) {
            case RemoveCrossedOff, RemoveAll, Copy, Move, Remove:
                doMoveRemoveItemOperations(sourceList, userId, sourceListId, operationType, tagIds, destinationListId);
                break;
            case CrossOff, UnCrossOff:
                doCrossOffActions(sourceList, operationType, tagIds);
                break;
        }
    }

    private void doCrossOffActions(ShoppingListEntity sourceList, ItemOperationType operationType, List<Long> tagIds) {
        // get item
        List<ListItemEntity> items = sourceList.getItems();

        Date crossOffDate = operationType.equals(ItemOperationType.CrossOff) ? new Date() : null;

        items.stream().filter(i -> i.getRemovedOn() == null)
                .filter(i -> tagIds.contains(i.getTag().getId()))
                .forEach(i -> i.setCrossedOff(crossOffDate));

        sourceList.setLastUpdate(new Date());
        itemRepository.saveAll(items);

    }

    public void doMoveRemoveItemOperations(ShoppingListEntity sourceList, Long userId, Long sourceListId, ItemOperationType operationType,
                                           List<Long> tagIds, Long destinationListId) throws ItemProcessingException {

        List<ListItemEntity> operationItems = null;
        if (operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {
            operationItems = getListItemsForOperationType(operationType, sourceList);
        } else {
            operationItems = sourceList.getItems().stream()
                    .filter(item -> tagIds.contains(item.getTag().getId()))
                    .toList();
        }

        if (operationItems == null || operationItems.isEmpty()) {
            return;
        }

        // if operation requires copy, get destinationList and copy
        if (operationType.equals(ItemOperationType.Copy) ||
                operationType.equals(ItemOperationType.Move)) {
            ShoppingListEntity targetList = getListForUserById(userId, destinationListId);
            Map<Long, ListItemEntity> destinationMap = targetList.getItems().stream()
                    .filter(item -> tagIds.contains(item.getTag().getId()))
                    .collect(Collectors.toMap(item -> item.getTag().getId(), item -> item));
            List<ListItemEntity> addedItems = new ArrayList<>();

            for (ListItemEntity item : operationItems) {
                ListItemEntity existingItem = destinationMap.get(item.getTag().getId());
                ItemStateContext context = new ItemStateContext(existingItem, destinationListId);
                context.setListItem(item);
                ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

                addedItems.add(result);
            }

            saveListChanges(targetList, addedItems, ListOperationType.LIST_ADD);
        }

        // if operation requires remove, remove from source
        if (operationType.equals(ItemOperationType.Move) ||
                operationType.equals(ItemOperationType.Remove) ||
                operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {

            List<ListItemEntity> changedItems = new ArrayList<>();
            List<Long> removedTagIds = new ArrayList<>();
            for (ListItemEntity item : operationItems) {
                ItemStateContext context = new ItemStateContext(item, sourceListId);
                context.setTag(item.getTag());
                ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context);
                if (result == null) {
                    removedTagIds.add(item.getTag().getId());
                } else {
                    changedItems.add(result);
                }
            }

            saveListChanges(sourceList, changedItems, removedTagIds, ListOperationType.LIST_REMOVE);
        }

    }

    @Override
    public void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException, ItemProcessingException {
        // retrieve list
        ShoppingListEntity list = getListForUserById(userId, listId);
        if (list == null) {
            throw new ObjectNotFoundException(String.format("No list found for user [%s] with list id [%s])", userId, listId));
        }

        // get dishes to add
        List<Long> dishIds = listAddProperties.getDishSourceIds();
        if (dishIds.isEmpty()) {
            return;
        }

        doAddDishesToList(userId, list, dishIds);
    }

    private void doAddDishesToList(Long userId, ShoppingListEntity list, List<Long> dishIds) throws ShoppingListException, ItemProcessingException {
        // get dish items
        List<DishItemEntity> dishItems = dishService.getDishItems(userId, dishIds);

        List<ListItemEntity> changedItems = addDishItemsToList(list, dishItems);

        saveListChanges(list, changedItems, ListOperationType.DISH_ADD);
    }

    @Override
    public ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGenerateProperties) throws ShoppingListException, ItemProcessingException {
        // check list name
        String listNameFromProperties = listGenerateProperties.getListName();
        if (listNameFromProperties == null || listNameFromProperties.isEmpty()) {
            listNameFromProperties = defaultShoppingListName;
        }
        String listName = ensureListNameIsUnique(userId, listNameFromProperties);
        // create list
        ShoppingListEntity newList = createList(userId, listName);

        // get dishes to add
        List<Long> dishIds = new ArrayList<>();
        if (listGenerateProperties.getDishSourcesIds() != null) {
            dishIds = listGenerateProperties.getDishSourcesIds();
        } else if (listGenerateProperties.getMealPlanSourceId() != null) {
            // get dishIds for meal plan
            MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, listGenerateProperties.getMealPlanSourceId());
            dishIds = new ArrayList<>();
            if (mealPlan.getSlots() != null) {
                for (SlotEntity slot : mealPlan.getSlots()) {
                    dishIds.add(slot.getDish().getId());
                }
            }
        }

        // now, add all dish ids
        doAddDishesToList(userId, newList, dishIds);

        // add starter list - if desired
        if (Boolean.TRUE.equals(listGenerateProperties.getAddFromStarter())) {
            // add Items from BaseList
            ShoppingListEntity baseList = getStarterList(userId);
            if (baseList != null) {
                doAddListToList(newList, baseList);
            }
        }

        // check about generating a meal plan
        generateMealPlanOnListCreate(userId, listGenerateProperties);

        // save changes
        saveListChanges(newList, newList.getItems(), ListOperationType.NONE);
        return newList;

    }

    private String ensureListNameIsUnique(Long userId, String listName) {
        // does this name already exist for the user?
        List<ShoppingListEntity> existing = shoppingListRepository.findByUserIdAndName(userId, listName);

        if (existing.isEmpty()) {
            return listName;
        }

        // if so, get all lists with names starting with the listName
        List<ShoppingListEntity> similar = shoppingListRepository.findByUserIdAndNameLike(userId, listName + "%");
        List<String> similarNames = similar.stream()
                .map(list -> list.getName().trim().toLowerCase()).toList();
        // use handy StringTools method to get first unique name

        return StringTools.makeUniqueName(listName, similarNames);
    }

    private void generateMealPlanOnListCreate(Long userId, ListGenerateProperties listGenerateProperties) {
        if (Boolean.TRUE.equals(listGenerateProperties.getGenerateMealplan()) &&
                listGenerateProperties.getMealPlanSourceId() == null &&
                listGenerateProperties.getDishSourcesIds() != null) {
            MealPlanEntity mp = mealPlanService.createMealPlan(userId, new MealPlanEntity());
            for (Long ds : listGenerateProperties.getDishSourcesIds()) {
                mealPlanService.addDishToMealPlan(userId, mp.getId(), ds);
            }
        }
    }

    @Override
    public ShoppingListEntity getStarterList(Long userId) {

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdAndIsStarterListTrue(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    @Override
    public ShoppingListEntity getMostRecentList(Long userId) {

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    @Override
    public ShoppingListEntity getListForUserById(Long userId, Long listId) {
        final String message = String.format("Retrieving List for id %d and user_id %s", listId, userId);
        logger.debug(message);

        Optional<ShoppingListEntity> shoppingListEntityOpt;
        shoppingListEntityOpt = shoppingListRepository.getWithItemsByListId(listId);

        // may be a list which doesn't have items.  Check for that here
        if (shoppingListEntityOpt.isEmpty()) {
            shoppingListEntityOpt = shoppingListRepository.findById(listId);
        }
        ShoppingListEntity shoppingListEntity = shoppingListEntityOpt.orElse(null);
        if (shoppingListEntity != null && shoppingListEntity.getUserId().equals(userId)) {
            return shoppingListEntity;
        }
        return null;
    }

    @Override
    public ShoppingListEntity getSimpleListForUserById(Long userId, Long listId) {
        final String message = String.format("Retrieving List for id %d and user_id %s", listId, userId);
        logger.debug(message);

        Optional<ShoppingListEntity> shoppingListEntityOpt;
        shoppingListEntityOpt = shoppingListRepository.findByListIdAndUserId(listId, userId);

        return shoppingListEntityOpt.orElse(null);
    }

    @Override
    @Transactional
    public void deleteList(Long userId, Long listId) {
        List<ShoppingListEntity> allLists = getListsByUserId(userId);
        if (allLists == null || allLists.isEmpty()) {
            throw new ActionInvalidException(String.format("No lists found for user [%s]", userId));
        }
        if (allLists.size() < 2) {
            throw new ActionInvalidException(String.format("Can't delete the last list for user [%s]", userId));
        }
        Optional<ShoppingListEntity> toDeleteOpt = allLists.stream()
                .filter(l -> l.getId().equals(listId)).findFirst();
        if (toDeleteOpt.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Can't find list [%s] for userName [%s] to delete.", listId, userId));
        }

        ShoppingListEntity toDelete = toDeleteOpt.get();
        List<ListItemEntity> items = itemRepository.findByListId(listId);
        itemRepository.deleteAll(items);
        toDelete.setItems(new ArrayList<>());
        shoppingListRepository.save(toDelete);
        toDelete = getSimpleListForUserById(userId, listId);
        shoppingListRepository.delete(toDelete.getId());
        shoppingListRepository.flush();
    }

    @Override
    public void addItemToListByTag(Long userId, Long listId, Long tagId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ListItemEntity item = shoppingListEntity.getItems().stream()
                .filter(l -> l.getTag().getId().equals(tagId))
                .findFirst()
                .orElse(null);
        boolean isNew = item == null;

        TagEntity tag = tagService.getTagById(tagId);
        ItemStateContext itemStateContext = new ItemStateContext(item, listId);
        itemStateContext.setTag(tag);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        if (isNew) {
            shoppingListEntity.getItems().add(result);
        }

        saveListChanges(shoppingListEntity,
                Collections.singletonList(result),
                ListOperationType.TAG_ADD);
    }

    @Override
    public void updateItemCount(Long userId, Long listId, Long tagId, Integer usedCount) {
        if (usedCount == null) {
            throw new ActionInvalidException("usedCount is null in updateItemCount.");
        }

        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ListItemEntity item = itemRepository.getItemByListAndTag(listId, tagId);
        if (item == null) {
            throw new ObjectNotFoundException("no item found in list [" + listId + "] with tagid [" + tagId + "]");
        }

        // set fields in item
        item.setUsedCount(usedCount);
        item.setUpdatedOn(new Date());
        item.setRemovedOn(null);
        item.setCrossedOff(null);

        // update item
        itemRepository.save(item);

        // update list date
        shoppingListEntity.setLastUpdate(new Date());
        shoppingListRepository.save(shoppingListEntity);
    }

    @Override
    public void deleteAllItemsFromList(Long userId, Long listId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getSimpleListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        List<ListItemEntity> itemEntities = itemRepository.findByListId(listId);
        if (itemEntities == null) {
            return;
        }

        List<ListItemEntity> updatedItems = new ArrayList<>();
        List<Long> deletedItems = new ArrayList<>();
        for (ListItemEntity item : itemEntities) {
            ItemStateContext context = new ItemStateContext(item, listId);
            context.setTag(item.getTag());
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context);
            if (result == null) {
                deletedItems.add(item.getTag().getId());
            } else {
                updatedItems.add(item);
            }
            updatedItems.add(result);
        }
        saveListChanges(shoppingListEntity, updatedItems, deletedItems, ListOperationType.NONE);
    }

    @Override
    public void deleteItemFromList(Long userId, Long listId, Long itemId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        Optional<ListItemEntity> itemEntityOpt = itemRepository.findById(itemId);
        if (itemEntityOpt.isEmpty()) {
            return;
        }
        ListItemEntity item = itemEntityOpt.get();
        ItemStateContext context = new ItemStateContext(item, listId);
        context.setTag(item.getTag());
        listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context);

        saveListChanges(shoppingListEntity, List.of(item), List.of(item.getTag().getId()), ListOperationType.LIST_REMOVE);
    }

    @Override
    public ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId) throws ShoppingListException, ItemProcessingException {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity savedNewList = createList(userId, defaultShoppingListName);

        // add to the new list
        return addToListFromMealPlan(userId, savedNewList, mealPlan);

    }

    @Override
    public void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId) throws ShoppingListException, ItemProcessingException {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        addToListFromMealPlan(userId, shoppingList, mealPlan);
    }

    private ShoppingListEntity addToListFromMealPlan(Long userId, ShoppingListEntity shoppingList, MealPlanEntity mealPlan) throws ShoppingListException, ItemProcessingException {
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(SlotEntity::getDish)
                .map(DishEntity::getId)
                .toList();

        // get dish items
        List<DishItemEntity> dishItems = dishService.getDishItems(userId, dishIds);

        List<ListItemEntity> changedItems = addDishItemsToList(shoppingList, dishItems);

        saveListChanges(shoppingList, changedItems, ListOperationType.DISH_ADD);
        Optional<ShoppingListEntity> shoppingListEntity = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(shoppingList.getId());
        return shoppingListEntity.orElse(null);
    }

    @Override
    public List<ShoppingListCategory> categorizeList(ShoppingListEntity shoppingListEntity) {

        if (shoppingListEntity == null) {
            return new ArrayList<>();
        }
        Long userLayoutId = determineUserLayout(shoppingListEntity.getUserId(), shoppingListEntity.getListLayoutId());

        // find frequently crossed off
        List<Long> frequentTagIds = listTagStatisticService.findFrequentIdsForList(shoppingListEntity.getId(), shoppingListEntity.getUserId());

        List<ItemMappingDTO> mappedItems = shoppingListRepository.getListMappings(userLayoutId, shoppingListEntity.getId());

        Map<String, List<ShoppingListItem>> itemLists = new HashMap<>();
        Map<String, ShoppingListCategory> filledCategories = new HashMap<>();

        mappedItems.stream()
                .filter(im -> im.getRemovedOn() == null)
                .forEach(im -> {
                    // create Item from ItemMapping
                    ShoppingListItem item = im.mapToShoppingListItem();

                    // add frequent handle
                    if (frequentTagIds.contains(im.getTagId())) {
                        item.addHandle(ShoppingListService.FREQUENT);
                    }
                    // handle category
                    ShoppingListCategory category;
                    if (filledCategories.containsKey(im.getCategoryName())) {
                        category = filledCategories.get(im.getCategoryName());
                        updateCategoryModelFromMapping(category, im);
                    } else {
                        category = createCategoryModelFromMapping(im);
                        filledCategories.put(category.getName(), category);
                    }
                    (itemLists.computeIfAbsent(im.getCategoryName(), k -> new ArrayList<>())).add(item);

                });

        // sort items in filled categories - content sort
        for (Map.Entry<String, List<ShoppingListItem>> entry : itemLists.entrySet()) {
            (entry.getValue()).sort(new ShoppingListItemComparator());
        }

        // prune and sort categories - sorts the categories themselves, not the contents
        List<ShoppingListCategory> result = new ArrayList<>();
        filledCategories.forEach((categoryname, category) -> {
            category.setItems(itemLists.get(categoryname));
            result.add(category);
        });

        // return list of categories
        result.sort(Comparator.comparing(ShoppingListCategory::getDisplayOrder));

        return result;
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

    private ShoppingListCategory createCategoryModelFromMapping(ItemMappingDTO itemMappingDTO) {

        ShoppingListCategory category = new ShoppingListCategory(itemMappingDTO.getCategoryId());
        category.setUserCategoryId(itemMappingDTO.getUserCategoryId());
        category.setName(itemMappingDTO.getCategoryName());
        category.setDisplayOrder(itemMappingDTO.getDisplayOrder());
        category.setUserDisplayOrder(itemMappingDTO.getUserDisplayOrder());

        return category;
    }

    private void updateCategoryModelFromMapping(ShoppingListCategory category, ItemMappingDTO itemMappingDTO) {
        category.setUserCategoryId(itemMappingDTO.getUserCategoryId());
        category.setName(itemMappingDTO.getCategoryName());
        category.setUserDisplayOrder(itemMappingDTO.getUserDisplayOrder());
    }

    @Override
    public MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest) {
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

        // delete tags by removed on
        LocalDate removedBeforeDate = LocalDate.now().minusDays(mergeDeleteAfterDays);
        List<ListItemEntity> itemsToRemove = itemRepository.findByRemovedOnBefore(java.sql.Date.valueOf(removedBeforeDate));
        itemRepository.deleteAll(itemsToRemove);

        logger.info("Merge complete for list [{}}].", list.getId());
        return new MergeResult();
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

    @Override
    public void addListToList(Long userId, Long listId, Long fromListId) throws ItemProcessingException {
        // get the target list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // get the list to add
        ShoppingListEntity toAdd = getListForUserById(userId, fromListId);
        if (toAdd == null) {
            return;
        }

        doAddListToList(list, toAdd);
    }

    private void doAddListToList(ShoppingListEntity targetList, ShoppingListEntity addFromList) throws ItemProcessingException {
        // get list items for list to add
        List<ListItemEntity> itemsToAdd = addFromList.getItems();

        // get hash of tag ids to list items for target list
        Map<Long, ListItemEntity> tagToItem = targetList.getItems().stream()
                .collect(Collectors.toMap(i -> i.getTag().getId(), item -> item));

        // go through all list items to add, adding item for each list
        List<ListItemEntity> newOrUpdatedListItems = new ArrayList<>();
        for (ListItemEntity itemToAdd : itemsToAdd) {
            ListItemEntity item = tagToItem.get(itemToAdd.getTag().getId());
            boolean isNew = item == null;
            ItemStateContext context = new ItemStateContext(item, targetList.getId());
            context.setListItem(itemToAdd);
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

            newOrUpdatedListItems.add(result);
            if (isNew) {
                targetList.getItems().add(result);
            }
        }

        // save list
        saveListChanges(targetList, newOrUpdatedListItems, ListOperationType.LIST_ADD);
    }


    @Override
    public void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException, ItemProcessingException {
        // get the list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // gather tags for dish to add
        if (dishId == null) {
            logger.error("No dish found for null dishId");
            throw new ShoppingListException("No dish found for null dishId.");
        }
        List<DishItemEntity> dishItems = tagService.getItemsForDish(userId, dishId);

        List<ListItemEntity> changedItems = addDishItemsToList(list, dishItems);

        saveListChanges(list, changedItems, ListOperationType.DISH_ADD);
    }

    @Override
    public void fillSources(ShoppingListEntity result) {
        // dish sources
        // gather distinct dish sources for list
        List<Long> dishIds = itemRepository.findDishSourcesForListFromItems(result.getId());

        if (dishIds != null && !dishIds.isEmpty()) {
            // retrieve dishes from database
            List<DishEntity> dishSources = dishService.getDishes(dishIds);
            // set in shopping list
            result.setDishSources(dishSources);
        }

        // list sources
        List<Long> listSourceIds = itemRepository.findListSourcesForListForDetails(result.getId());

        // gather distinct list sources for list
        if (listSourceIds != null && !listSourceIds.isEmpty()) {
            List<ShoppingListEntity> sourceLists = shoppingListRepository.findAllById(listSourceIds);
            // set in shopping list
            result.setListSources(sourceLists);
        }
    }

    @Override
    public void changeListLayout(Long userId, Long listId, Long layoutId) {
        // get shopping list
        ShoppingListEntity shoppingList = getSimpleListForUserById(userId, listId);
        // set new layout id in shopping list
        shoppingList.setListLayoutId(layoutId);
        // save shopping list
        shoppingListRepository.save(shoppingList);

    }

    @Override
    public void removeDishFromList(Long userId, Long listId, Long dishId) throws ItemProcessingException {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        List<Long> tagIdsToRemove = itemRepository.findTagIdsInListByDishId(dishId, listId);

        List<ListItemEntity> changedItems = new ArrayList<>();
        List<Long> removedTagIds = new ArrayList<>();
        for (ListItemEntity item : shoppingList.getItems()) {
            Long tagId = item.getTag().getId();
            if (tagIdsToRemove.contains(tagId)) {
                removeDishItem(item, listId, dishId, changedItems, removedTagIds);
            }
        }
        saveListChanges(shoppingList, changedItems, removedTagIds, ListOperationType.DISH_REMOVE);
    }

    private void removeDishItem(ListItemEntity item, Long listId, Long dishId, List<ListItemEntity> changedItems, List<Long> removedIds) throws ItemProcessingException {
        ItemStateContext context = new ItemStateContext(item, listId);
        context.setDishId(dishId);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context);
        if (result == null) {
            removedIds.add(item.getTag().getId());
        } else {
            changedItems.add(result);
        }
    }

    @Override
    public void removeListItemsFromList(Long userId, Long listId, Long fromListId) throws ItemProcessingException {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);
        Map<Long, ListItemEntity> listItemsByTag = shoppingList.getItems().stream()
                .filter(item -> item.getTag().getId() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), item -> item));

        // get list tag ids to remove
        List<Long> tagIdsToRemove = itemRepository.findTagIdsInListByListId(fromListId, listId);

        List<ListItemEntity> changedItems = new ArrayList<>();
        List<Long> removedItems = new ArrayList<>();
        for (Long tagId : tagIdsToRemove) {
            ListItemEntity itemInList = listItemsByTag.get(tagId);
            ItemStateContext testContext = new ItemStateContext(itemInList, listId);
            testContext.setListId(fromListId);
            ListItemEntity resultItems = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext);
            if (resultItems != null) {
                changedItems.add(itemInList);
            } else {
                removedItems.add(itemInList.getTag().getId());
            }
        }

        saveListChanges(shoppingList, changedItems, removedItems, ListOperationType.LIST_REMOVE);
    }

    @Override
    public void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff) throws ItemProcessingException {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        Optional<ListItemEntity> itemOpt = shoppingListEntity.getItems().stream()
                .filter(li -> li.getId().equals(itemId))
                .findFirst();
        if (itemOpt.isEmpty()) {
            return;
        }
        ListItemEntity item = itemOpt.get();

        // ensure item belongs to list
        if (item.getListId() == null && !item.getListId().equals(shoppingListEntity.getId())) {
            return;
        }

        ItemStateContext context = new ItemStateContext(item, listId);
        context.setCrossedOff(crossedOff);
        ListItemEntity changedItem = listItemStateMachine.handleEvent(ListItemEvent.CROSS_OFF_ITEM, context);


        saveListChanges(shoppingListEntity, List.of(changedItem), ListOperationType.NONE);
    }

    @Override
    public void crossOffAllItems(Long userId, Long listId, boolean crossOff) throws ItemProcessingException {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        List<ListItemEntity> items = shoppingListEntity.getItems();

        for (ListItemEntity item : items) {
            ItemStateContext context = new ItemStateContext(item, listId);
            context.setCrossedOff(crossOff);
            listItemStateMachine.handleEvent(ListItemEvent.CROSS_OFF_ITEM, context);

        }

        saveListChanges(shoppingListEntity, items, ListOperationType.NONE);
        itemRepository.saveAll(items);
    }

    private void legacySaveListChanges(ShoppingListEntity shoppingList, ItemCollector collector, CollectorContext context) {
        itemChangeRepository.legacySaveItemChanges(shoppingList, collector, shoppingList.getUserId(), context);
        // make changes in list object
        shoppingList.setItems(collector.getAllItems());
        if (collector.hasChanges()) {
            shoppingList.setLastUpdate(new Date());
        }
        shoppingListRepository.save(shoppingList);
    }

    private void saveListChanges(ShoppingListEntity shoppingList, List<ListItemEntity> items,
                                 ListOperationType operationType) {
        itemChangeRepository.saveItemChangeStatistics(shoppingList, items, Collections.emptyList(), shoppingList.getUserId(), operationType);
        // make changes in list object
        if (items != null && !items.isEmpty()) {
            shoppingList.setLastUpdate(new Date());
            shoppingListRepository.save(shoppingList);
        }
    }


    private void saveListChanges(ShoppingListEntity shoppingList, List<ListItemEntity> changedItems,
                                 List<Long> removedTagIds, ListOperationType operationType) {
        itemChangeRepository.saveItemChangeStatistics(shoppingList, changedItems, removedTagIds, shoppingList.getUserId(), operationType);

        List<ListItemEntity> filteredItems = shoppingList.getItems().stream()
                .filter(item -> !removedTagIds.contains(item.getTag().getId()))
                .collect(Collectors.toList());
        shoppingList.setItems(filteredItems);
        // make changes in list object
        if ((changedItems != null && !changedItems.isEmpty()) ||
                ( removedTagIds != null && !removedTagIds.isEmpty())) {
            shoppingList.setLastUpdate(new Date());
            shoppingListRepository.save(shoppingList);
        }
    }

    private void checkReplaceTagsInCollector(ItemCollector mergeCollector) {
        Set<Long> allServerTagIds = new HashSet<>(mergeCollector.getAllTagIds());

        if (allServerTagIds.isEmpty()) {
            return;
        }
        List<TagEntity> outdatedTags = tagService.getReplacedTagsFromIds(allServerTagIds);
        if (!outdatedTags.isEmpty()) {
            Set<Long> outdatedIds = outdatedTags.stream().map(TagEntity::getReplacementTagId).collect(Collectors.toSet());
            Map<Long, TagEntity> outdatedDictionary = tagService.getDictionaryForIds(outdatedIds);

            mergeCollector.replaceOutdatedTags(outdatedTags, outdatedDictionary);
        }

    }

    private List<ListItemEntity> convertClientItemsToItemEntities(Long userId, MergeRequest mergeRequest) {
        Map<String, ListItemEntity> mergeMap = mergeRequest.getMergeItems().stream()
                .filter(i -> i.getTagId() != null)
                .collect(Collectors.toMap(Item::getTagId, ModelMapper::toEntity));
        Set<Long> tagKeys = mergeMap.keySet().stream().map(Long::valueOf).collect(Collectors.toSet());

        if (tagKeys.isEmpty()) {
            return new ArrayList<>();
        }
        if (mergeRequest.isCheckTagConflict()) {
            checkTagConflict(userId, tagKeys, mergeMap);
        }
        List<TagEntity> outdatedClientTags = tagService.getReplacedTagsFromIds(tagKeys);
        Map<Long, TagEntity> outdatedClientDictionary = new HashMap<>();
        if (!outdatedClientTags.isEmpty()) {
            Set<Long> outdatedIds = outdatedClientTags.stream().map(TagEntity::getReplacementTagId).collect(Collectors.toSet());
            outdatedClientDictionary = tagService.getDictionaryForIds(outdatedIds);
        }
        Map<Long, TagEntity> tagDictionary = tagService.getDictionaryForIds(mergeMap.keySet().stream()
                .map(Long::valueOf).collect(Collectors.toSet()));

        Map<Long, ListItemEntity> itemMap = new HashMap<>();
        for (Map.Entry<String, ListItemEntity> entry : mergeMap.entrySet()) {
            String tagIdString = entry.getKey();
            ListItemEntity item = entry.getValue();
            Long tagId = Long.valueOf(tagIdString);
            TagEntity tag = tagDictionary.get(tagId);
            if (!outdatedClientDictionary.isEmpty() && tag.getReplacementTagId() != null) {
                TagEntity replacementTag = outdatedClientDictionary.get(tag.getReplacementTagId());
                item.setTag(replacementTag);
                addItemToClientMap(item, itemMap);
                continue;
            }
            item.setTag(tag);
            addItemToClientMap(item, itemMap);
        }

        return new ArrayList<>(itemMap.values());
    }

    private void checkTagConflict(Long userId, Set<Long> tagKeys, Map<String, ListItemEntity> mergeMap) {
        List<LongTagIdPairDTO> conflicts = tagService.getStandardUserDuplicates(userId, tagKeys);
        for (LongTagIdPairDTO conflict : conflicts) {
            ListItemEntity replaceItem = mergeMap.get(String.valueOf(conflict.getLeftId()));
            if (replaceItem != null) {

                replaceItem.setTagId(conflict.getRightId());
                if (replaceItem.getTag() != null) {
                    replaceItem.getTag().setId(conflict.getRightId());
                }
                mergeMap.put(String.valueOf(conflict.getRightId()), replaceItem);
                mergeMap.remove(String.valueOf(conflict.getLeftId()));
            }
        }

    }

    private void addItemToClientMap(ListItemEntity item, Map<Long, ListItemEntity> itemMap) {
        if (item.getTag() == null) {
            return;
        }
        Long tagId = item.getTag().getId();
        ListItemEntity toAddTo = itemMap.get(tagId);
        if (itemMap.containsKey(tagId)) {
            int count = toAddTo.getUsedCount() != null ? toAddTo.getUsedCount() : 0;
            toAddTo.setUsedCount(count + 1);
            toAddTo.setRemovedOn(DateUtils.maxDate(toAddTo.getRemovedOn(), item.getRemovedOn()));
            toAddTo.setCrossedOff(DateUtils.maxDate(toAddTo.getCrossedOff(), item.getCrossedOff()));
            toAddTo.setUpdatedOn(DateUtils.maxDate(toAddTo.getUpdatedOn(), item.getUpdatedOn()));
            toAddTo.setAddedOn(DateUtils.maxDate(toAddTo.getAddedOn(), item.getAddedOn()));
            itemMap.put(tagId, toAddTo);
            return;
        }
        itemMap.put(tagId, item);
    }

    private List<ListItemEntity> addDishItemsToList(ShoppingListEntity shoppingList, List<DishItemEntity> dishItems) throws ShoppingListException, ItemProcessingException {
        List<ListItemEntity> items = shoppingList.getItems();
        // gather tags for dish to add
        if (dishItems == null || dishItems.isEmpty()) {
            return new ArrayList<>();
        }

        // tag ids for dish items
        Set<Long> tagIdsInDish = dishItems.stream()
                .map(DishItemEntity::getTag)
                .map(TagEntity::getId)
                .collect(Collectors.toSet());

        // create hash of tag_id to list_items
        Map<Long, ListItemEntity> tagToItem = items.stream()
                .filter(item -> tagIdsInDish.contains(item.getTag().getId()))
                .collect(Collectors.toMap(i -> i.getTag().getId(), item -> item));

        List<ListItemEntity> newOrUpdatedListItems = new ArrayList<>();
        List<Long> addedDishIds = new ArrayList<>();
        for (DishItemEntity dishItemToAdd : dishItems) {

            ListItemEntity item = tagToItem.get(dishItemToAdd.getTag().getId());
            boolean isNew = item == null;
            ItemStateContext context = new ItemStateContext(item, shoppingList.getId());
            context.setDishItem(dishItemToAdd);
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

            addedDishIds.add(dishItemToAdd.getDish().getId());
            newOrUpdatedListItems.add(result);
            if (isNew) {
                shoppingList.getItems().add(result);
                tagToItem.put(dishItemToAdd.getTag().getId(), result);
            }

        }
        // update last added date for dish
        this.dishService.updateLastAddedForDishes(addedDishIds);

        return newOrUpdatedListItems;
    }

    private ShoppingListEntity createList(Long userId, String listName) {
        ShoppingListEntity newList = new ShoppingListEntity();

        ListLayoutEntity listLayout = listLayoutService.getDefaultUserLayout(userId);
        if (listLayout != null) {
            newList.setListLayoutId(listLayout.getId());
        }
        newList.setName(listName);
        newList.setIsStarterList(false);
        newList.setCreatedOn(new Date());
        newList.setUserId(userId);
        return shoppingListRepository.save(newList);
    }

    private List<ListItemEntity> getListItemsForOperationType(ItemOperationType operationType, ShoppingListEntity sourceList) {
        if (operationType.equals(ItemOperationType.RemoveCrossedOff)) {
            return sourceList.getItems().stream()
                    .filter(item -> item.getCrossedOff() != null)
                    .toList();
        } else if (operationType.equals(ItemOperationType.RemoveAll)) {
            return sourceList.getItems();
        }
        return new ArrayList<>();
    }

}
