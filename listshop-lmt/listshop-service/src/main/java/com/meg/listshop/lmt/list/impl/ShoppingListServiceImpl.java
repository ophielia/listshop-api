package com.meg.listshop.lmt.list.impl;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.ListItemCollector;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.list.ShoppingListException;
import com.meg.listshop.lmt.list.ShoppingListService;
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
@Transactional
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingListServiceImpl.class);

    private final TagService tagService;
    private final DishService dishService;
    private final
    ShoppingListRepository shoppingListRepository;
    private final
    LayoutService listLayoutService;
    private final
    MealPlanService mealPlanService;
    private final
    ItemRepository itemRepository;
    private final
    ListTagStatisticService listTagStatisticService;

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
                                   ListTagStatisticService listTagStatisticService) {
        this.tagService = tagService;
        this.dishService = dishService;
        this.shoppingListRepository = shoppingListRepository;
        this.listLayoutService = listLayoutService;
        this.mealPlanService = mealPlanService;
        this.itemRepository = itemRepository;
        this.itemChangeRepository = itemChangeRepository;
        this.listTagStatisticService = listTagStatisticService;
    }


    @Override
    public List<ShoppingListEntity> getListsByUserId(Long userId) {
        return shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId);
    }


    public ShoppingListEntity updateList(Long userId, Long listId, ShoppingListEntity updateFrom) {
        // get list
        Optional<ShoppingListEntity> byUserNameAndId = shoppingListRepository.findByListIdAndUserId(listId, userId);
        if (!byUserNameAndId.isPresent()) {
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

    public void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) {
        logger.debug("Beginning performItemOperation with sourceListId [{}], destinationListId[{}],  tagIds [{}] and itemOperationType [{}]", sourceListId, destinationListId, tagIds, operationType);
        // get source list
        ShoppingListEntity sourceList = getListForUserById(userId, sourceListId);

        if (sourceList == null) {
            return;
        }
        switch (operationType) {
            case RemoveCrossedOff:
            case RemoveAll:
            case Copy:
            case Move:
            case Remove:
                doMoveRemoveItemOperations(sourceList, userId, sourceListId, operationType, tagIds, destinationListId);
                break;
            case CrossOff:
            case UnCrossOff:
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

    public void doMoveRemoveItemOperations(ShoppingListEntity sourceList, Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) {

        if (operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {
            tagIds = getTagIdsForOperationType(operationType, sourceList);
        }

        if (tagIds == null) {
            return;
        }

        // convert tagids to tag entities
        Set<Long> tagSet = new HashSet<>(tagIds);
        Map<Long, TagEntity> tagDictionary = tagService.getDictionaryForIds(tagSet);
        List<TagEntity> tagList = new ArrayList<>(tagDictionary.values());

        // if operation requires copy, get destinationList and copy
        if (operationType.equals(ItemOperationType.Copy) ||
                operationType.equals(ItemOperationType.Move)) {
            ShoppingListEntity targetList = getListForUserById(userId, destinationListId);
            List<ListItemEntity> items = targetList.getItems();


            CollectorContext context = new CollectorContextBuilder().create(ContextType.List)
                    .withListId(sourceListId)
                    .withRemoveEntireItem(true)
                    .withKeepExistingCrossedOffStatus(true)
                    .doCopyCrossedOff(true)
                    .withStatisticCountType(StatisticCountType.Single)
                    .build();
            ListItemCollector collector = createListItemCollector(destinationListId, items);
            collector.copyExistingItemsIntoList(itemsForTags(tagList, sourceList.getId()), context);
            saveListChanges(targetList, collector, context);
        }

        // if operation requires remove, remove from source
        if (operationType.equals(ItemOperationType.Move) ||
                operationType.equals(ItemOperationType.Remove) ||
                operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {


            List<ListItemEntity> items = sourceList.getItems();
            CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                    .withStatisticCountType(StatisticCountType.Single)
                    .withRemoveEntireItem(true).build();
            ListItemCollector collector = createListItemCollector(sourceListId, items);
            collector.removeItemsByTagIds(tagIds, context);
            saveListChanges(sourceList, collector, context);
        }

    }

    private List<ListItemEntity> itemsForTags(List<TagEntity> tagList, Long sourceListId) {
        Map<Long, Long> tagMap = tagList.stream().collect(Collectors.toMap(TagEntity::getId, TagEntity::getId));
        List<ListItemEntity> listItems = itemRepository.findByListId(sourceListId);
        return listItems.stream()
                .filter(t -> t.getTag() != null)
                .filter(t -> tagMap.containsKey(t.getTag().getId())).collect(Collectors.toList());
    }

    private List<Long> getTagIdsForOperationType(ItemOperationType operationType, ShoppingListEntity sourceList) {
        if (operationType.equals(ItemOperationType.RemoveCrossedOff)) {
            return sourceList.getItems().stream()
                    .filter(item -> item.getCrossedOff() != null)
                    .map(ListItemEntity::getTag)
                    .filter(Objects::nonNull)
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
        } else if (operationType.equals(ItemOperationType.RemoveAll)) {
            return sourceList.getItems().stream()
                    .map(ListItemEntity::getTag)
                    .filter(Objects::nonNull)
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException {
        // retrieve list
        ShoppingListEntity newList = getListForUserById(userId, listId);
        if (newList == null) {
            throw new ObjectNotFoundException(String.format("No list found for user [%s] with list id [%s])", userId, listId));
        }
        ListItemCollector collector = createListItemCollector(newList.getId(), null);

        // get dishes to add
        List<Long> dishIds = listAddProperties.getDishSourceIds();
        if (dishIds.isEmpty()) {
            return;
        }

        // now, add all dish ids
        for (Long id : dishIds) {
            addDishToList(userId, collector, id);
        }

        // save changes
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(newList, collector, context);
    }


    @Override
    public ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGenerateProperties) throws ShoppingListException {
        // check list name
        String listNameFromProperties = listGenerateProperties.getListName();
        if (listNameFromProperties == null || listNameFromProperties.isEmpty()) {
            listNameFromProperties = defaultShoppingListName;
        }
        String listName = ensureListNameIsUnique(userId, listNameFromProperties);
        // create list
        ShoppingListEntity newList = createList(userId, listName);
        ListItemCollector collector = createListItemCollector(newList.getId(), null);

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
        for (Long id : dishIds) {
            addDishToList(userId, collector, id);
        }

        // add starter list - if desired
        if (Boolean.TRUE.equals(listGenerateProperties.getAddFromStarter())) {
            // add Items from BaseList
            ShoppingListEntity baseList = getStarterList(userId);
            if (baseList != null) {
                CollectorContext context = new CollectorContextBuilder().create(ContextType.List)
                        .withListId(baseList.getId())
                        .withStatisticCountType(StatisticCountType.StarterList)
                        .build();
                collector.copyExistingItemsIntoList(baseList.getItems(), context);
            }
        }

        // check about generating a meal plan
        generateMealPlanOnListCreate(userId, listGenerateProperties);

        // save changes
        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                .withStatisticCountType(StatisticCountType.List)
                .build();
        saveListChanges(newList, collector, context);
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
                .map(list -> list.getName().trim().toLowerCase()).collect(Collectors.toList());
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
    public void addItemToListByTag(Long userId, Long listId, Long tagId) {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        List<ListItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        // fill in tag, if item contains tag
        TagEntity tag;
        tag = tagService.getTagById(tagId);
        ListItemEntity item = new ListItemEntity();
        item.setTag(tag);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                .withStatisticCountType(StatisticCountType.Single).build();
        collector.addItem(item, context);

        saveListChanges(shoppingListEntity, collector, context);

    }

    //@Override
    public void NEWaddItemToListByTag(Long userId, Long listId, Long tagId) {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        List<ListItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        // fill in tag, if item contains tag
        TagEntity tag;
        tag = tagService.getTagById(tagId);
        ListItemEntity item = new ListItemEntity();
        item.setTag(tag);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                .withStatisticCountType(StatisticCountType.Single).build();
        collector.addItem(item, context);

        saveListChanges(shoppingListEntity, collector, context);

    }


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
    public void deleteAllItemsFromList(Long userId, Long listId) {
        ShoppingListEntity shoppingListEntity = getSimpleListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        List<ListItemEntity> itemEntities = itemRepository.findByListId(listId);
        if (itemEntities == null) {
            return;
        }


        ListItemCollector collector = createListItemCollector(listId, itemEntities);
        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.NonSpecified)
                .withListId(listId)
                .withStatisticCountType(StatisticCountType.List)
                .withRemoveEntireItem(true)
                .build();
        collector.removeItemsFromList(itemEntities, context);

        saveListChanges(shoppingListEntity, collector, context);
    }

    @Override
    public void deleteItemFromList(Long userId, Long listId, Long itemId, Boolean removeEntireItem, Long dishSourceId) {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        Optional<ListItemEntity> itemEntityOpt = itemRepository.findById(itemId);
        if (itemEntityOpt.isEmpty()) {
            return;
        }
        List<ListItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        ListItemEntity item = itemEntityOpt.get();
        if (item.getTag() == null) {
            collector.removeFreeTextItem(item);
        }
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishSourceId)
                .withRemoveEntireItem(removeEntireItem)
                .withStatisticCountType(StatisticCountType.Single)
                .build();
        collector.removeItemByTagId(item.getTag().getId(), context);

        saveListChanges(shoppingListEntity, collector, context);
    }

    @Override
    public ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId) {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity savedNewList = createList(userId, defaultShoppingListName);

        // add to the new list
        return addToListFromMealPlan(userId, savedNewList, mealPlan);

    }

    public void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId) {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        addToListFromMealPlan(userId, shoppingList, mealPlan);
    }

    private ShoppingListEntity addToListFromMealPlan(Long userId, ShoppingListEntity shoppingList, MealPlanEntity mealPlan) {
        // create the collector
        ListItemCollector collector = createListItemCollector(shoppingList.getId(), shoppingList.getItems());

        // add all tags for meal plan
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        for (SlotEntity slot : mealPlan.getSlots()) {
            CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                    .withDishId(slot.getDish().getId())
                    .withStatisticCountType(StatisticCountType.Dish)
                    .build();
            List<TagEntity> tags = tagService.getTagsForDish(userId, slot.getDish().getId(), tagTypeList);
            collector.addTags(tags, context);
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(shoppingList, collector, context);
        Optional<ShoppingListEntity> shoppingListEntity = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(shoppingList.getId());
        return shoppingListEntity.orElse(null);

    }

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

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
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
        saveListChanges(list, mergeCollector, context);

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
    public void addListToList(Long userId, Long listId, Long fromListId) {
        // get the target list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // get the list to add
        ShoppingListEntity toAdd = getListForUserById(userId, fromListId);
        if (toAdd == null) {
            return;
        }

        // create collector
        ListItemCollector collector = createListItemCollector(listId, list.getItems());

        // add Items from PickUpList
        CollectorContext context = new CollectorContextBuilder().create(ContextType.List)
                .withListId(fromListId)
                .withStatisticCountType(StatisticCountType.List)
                .build();
        collector.copyExistingItemsIntoList(toAdd.getItems(), context);

        // save list
        saveListChanges(list, collector, context);
    }

    public void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException {
        // get the list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // create collector
        ListItemCollector collector = createListItemCollector(listId, list.getItems());

        addDishToList(userId, collector, dishId);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(list, collector, context);
    }

    @Override
    public void fillSources(ShoppingListEntity result) {
        //MM rework this to look at new tables.  Should be a bit more direct

        // dish sources
        // gather distinct dish sources for list
        List<String> rawSources = itemRepository.findDishSourcesForList(result.getId());

        if (rawSources != null) {
            String source;
            if (rawSources.size() == 1) {
                source = rawSources.get(0);
            } else {
                source = String.join(";", rawSources);
            }
            // put into set
            Set<Long> dishIdSet = FlatStringUtils.inflateStringToLongSet(source, ";");
            List<Long> dishIds = new ArrayList<>(dishIdSet);
            // retrieve dishes from database
            List<DishEntity> dishSources = dishService.getDishes(dishIds);
            // set in shopping list
            result.setDishSources(dishSources);
        }

        // list sources
        List<String> listRawSources = itemRepository.findListSourcesForList(result.getId());

        // gather distinct list sources for list
        if (listRawSources != null) {
            String source;
            if (listRawSources.size() == 1) {
                source = listRawSources.get(0);
            } else {
                source = String.join(";", listRawSources);
            }
            // put into set
            Set<String> listSourceSet = FlatStringUtils.inflateStringToSet(source, ";");
            List<Long> sourceListIds = listSourceSet.stream()
                    .filter(val -> !val.isEmpty())
                    .map(Long::valueOf)
                    .filter(val -> val >= 0)
                    .collect(Collectors.toList());
            if (!sourceListIds.isEmpty()) {
                List<ShoppingListEntity> sourceLists = shoppingListRepository.findAllById(sourceListIds);
                // set in shopping list
                result.setListSources(sourceLists);

            }
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
    public void removeDishFromList(Long userId, Long listId, Long dishId) {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        // make collector
        ListItemCollector collector = createListItemCollector(listId, shoppingList.getItems());

        List<DishItemEntity> itemsToRemove = tagService.getItemsForDish(userId, dishId);
        List<TagEntity> tagsToRemove = itemsToRemove.stream().map(DishItemEntity::getTag).collect(Collectors.toList());

        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withRemoveEntireItem(false)
                .withKeepExistingCrossedOffStatus(true)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        collector.removeTagsForDish(dishId, tagsToRemove, context);
        saveListChanges(shoppingList, collector, context);

    }

    @Override
    public void removeListItemsFromList(Long userId, Long listId, Long fromListId) {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        // get list to remove
        ShoppingListEntity listToRemove = getListForUserById(userId, fromListId);

        // make collector
        ListItemCollector collector = createListItemCollector(listId, shoppingList.getItems());
        CollectorContext context = new CollectorContextBuilder().create(ContextType.List)
                .withListId(fromListId)
                .withRemoveEntireItem(false)
                .withStatisticCountType(StatisticCountType.List)
                .build();

        collector.removeItemsFromList(listToRemove.getItems(), context);

        saveListChanges(shoppingList, collector, context);
    }

    @Override
    public void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff) {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        Optional<ListItemEntity> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return;
        }
        ListItemEntity item = itemOpt.get();

        // ensure item belongs to list
        if (item.getListId() == null && !item.getListId().equals(shoppingListEntity.getId())) {
            return;
        }

        // set crossed off for item - by setting crossedOff date
        if (Boolean.TRUE.equals(crossedOff)) {
            Date updateDate = new Date();
            item.setCrossedOff(updateDate);
            item.setUpdatedOn(updateDate);
            shoppingListEntity.setLastUpdate(new Date());
        } else {
            item.setCrossedOff(null);
        }

        itemRepository.save(item);
        shoppingListRepository.save(shoppingListEntity);
    }

    @Override
    public void crossOffAllItems(Long userId, Long listId, boolean crossOff) {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        List<ListItemEntity> items = shoppingListEntity.getItems();

        Date crossOffDate = crossOff ? new Date() : null;

        items.stream().filter(i -> i.getRemovedOn() == null)
                .forEach(i -> i.setCrossedOff(crossOffDate));

        if (crossOff) {
            shoppingListEntity.setLastUpdate(new Date());
        }
        itemRepository.saveAll(items);
    }

    private void saveListChanges(ShoppingListEntity shoppingList, ItemCollector collector, CollectorContext context) {
        itemChangeRepository.saveItemChanges(shoppingList, collector, shoppingList.getUserId(), context);
        // make changes in list object
        shoppingList.setItems(collector.getAllItems());
        if (collector.hasChanges()) {
            shoppingList.setLastUpdate(new Date());
        }
        shoppingListRepository.save(shoppingList);
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

    private ListItemCollector createListItemCollector(Long listId, List<ListItemEntity> items) {
        ListItemCollector collector = new ListItemCollector(listId, items);
        checkReplaceTagsInCollector(collector);
        return collector;
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

    private void addDishToList(Long userId, ListItemCollector collector, Long dishId) throws ShoppingListException {
        // gather tags for dish to add
        if (dishId == null) {
            logger.error("No dish found for null dishId");
            throw new ShoppingListException("No dish found for null dishId.");
        }

        List<DishItemEntity> itemsToRemove = tagService.getItemsForDish(userId, dishId);

        List<TagEntity> tagsForDish = itemsToRemove.stream()
                .map(DishItemEntity::getTag)
                .filter(t -> t.getTagType().equals(TagType.Ingredient) || t.getTagType().equals(TagType.NonEdible))
                .collect(Collectors.toList());
        if (tagsForDish.isEmpty()) {
            final String message = String.format("No tags found for dishId %d", dishId);
            logger.info(message);
            return;
        }


        // add new dish tags to list
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        collector.addTags(tagsForDish, context);

        // update last added date for dish
        this.dishService.updateLastAddedForDish(dishId);
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

}
