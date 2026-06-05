/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.v2.impl;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.api.model.v2.Amount;
import com.meg.listshop.lmt.api.model.v2.MergeRequest;
import com.meg.listshop.lmt.api.model.v2.MergeResult;
import com.meg.listshop.lmt.api.model.v2.SourceReferenceType;
import com.meg.listshop.lmt.conversion.BasicAmount;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.*;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.BaseShoppingListService;
import com.meg.listshop.lmt.list.LegacyShoppingListService;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.list.v2.ShoppingListService;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public ShoppingListServiceImpl(TagService tagService,
                                   DishService dishService,
                                   ShoppingListRepository shoppingListRepository,
                                   @Qualifier("V2LayoutService") LayoutService listLayoutService,
                                   MealPlanService mealPlanService,
                                   ItemRepository itemRepository,
                                   ItemChangeRepository itemChangeRepository,
                                   ListTagStatisticService listTagStatisticService,
                                   ListItemStateMachine listItemStateMachine) {
        super(tagService, dishService, shoppingListRepository, listLayoutService,
                mealPlanService, itemRepository, itemChangeRepository, listTagStatisticService, listItemStateMachine);
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

        logger.info("Merge complete for list [{}}].", list.getId());
        return new MergeResult();

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

        TagEntity tag = tagService.getTagById(tagId);
        BasicAmount amount = pullAmountFromSimpleItem(itemDTO, tag);
        ItemStateContext itemStateContext = new ItemStateContext(item, listId);
        itemStateContext.setTag(tag);
        itemStateContext.setTagAmount(amount);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, userId);

        if (isNew) {
            shoppingListEntity.getItems().add(result);
        }

        saveListChanges(shoppingListEntity,
                Collections.singletonList(result),
                ListOperationType.TAG_ADD);
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
            Long listId = foundLists.get(0).getListId();
            return shoppingListRepository.findDTOById(listId);
        }
        return null;
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


}
