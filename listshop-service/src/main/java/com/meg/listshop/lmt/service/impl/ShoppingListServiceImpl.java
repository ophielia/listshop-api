package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.repository.ItemChangeRepository;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.categories.ItemCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListShopCategory;
import com.meg.listshop.lmt.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(ShoppingListServiceImpl.class);

    private final
    UserService userService;

    private final TagService tagService;
    private final DishService dishService;
    private final
    ShoppingListRepository shoppingListRepository;
    private final
    ListLayoutService listLayoutService;
    private final
    ListSearchService listSearchService;
    private final
    MealPlanService mealPlanService;
    private final
    ItemRepository itemRepository;
    private final
    ListTagStatisticService listTagStatisticService;

    private final
    ItemChangeRepository itemChangeRepository;

    private final ShoppingListProperties shoppingListProperties;

    @Value("${service.shoppinglistservice.merge.items.deleted.after.days}")
    int mergeDeleteAfterDays = 6;

    @Value("${service.shoppinglistservice.default.list.name}")
    String defaultShoppingListName;

    @Autowired
    public ShoppingListServiceImpl(UserService userService,
                                   TagService tagService,
                                   DishService dishService,
                                   ShoppingListRepository shoppingListRepository,
                                   ListLayoutService listLayoutService,
                                   ListSearchService listSearchService,
                                   MealPlanService mealPlanService,
                                   ItemRepository itemRepository,
                                   ItemChangeRepository itemChangeRepository,
                                   ShoppingListProperties shoppingListProperties,
                                   ListTagStatisticService listTagStatisticService) {
        this.userService = userService;
        this.tagService = tagService;
        this.dishService = dishService;
        this.shoppingListRepository = shoppingListRepository;
        this.listLayoutService = listLayoutService;
        this.listSearchService = listSearchService;
        this.mealPlanService = mealPlanService;
        this.itemRepository = itemRepository;
        this.itemChangeRepository = itemChangeRepository;
        this.shoppingListProperties = shoppingListProperties;
        this.listTagStatisticService = listTagStatisticService;
    }

    @Override
    public List<ShoppingListEntity> getListsByUsername(String userName) {
        UserEntity user = userService.getUserByUserEmail(userName);

        return shoppingListRepository.findByUserIdOrderByLastUpdateDesc(user.getId());
    }

    @Override
    public List<ItemEntity> getChangedItemsForMostRecentList(String name, Date changedAfter, Long layoutId) {
        //MM placeholder (called from depracated) - because this could be interesting - but not yet called.
        ShoppingListEntity shoppingListEntity = getMostRecentList(name);

        return itemRepository.getItemsChangedAfter(changedAfter, shoppingListEntity.getId());
    }

    @Override
    public ShoppingListEntity updateList(String name, Long listId, ShoppingListEntity updateFrom) {
        UserEntity user = userService.getUserByUserEmail(name);

        // get list
        List<ShoppingListEntity> byUserNameAndId = shoppingListRepository.findByListIdAndUserId(listId, user.getId());
        if (byUserNameAndId.isEmpty()) {
            throw new ObjectNotFoundException("List [" + listId + "] not found for user [" + user.getId() + "] in updateList");
        }
        ShoppingListEntity copyTo = byUserNameAndId.get(0);

        // check starter list change
        boolean starterListChanged = updateFrom.getIsStarterList() && !copyTo.getIsStarterList();

        // copy fields from updateFrom
        copyTo.setIsStarterList(updateFrom.getIsStarterList());
        copyTo.setName(updateFrom.getName());

        if (starterListChanged) {
            ShoppingListEntity oldStarter = getStarterList(name);
            if (oldStarter != null && oldStarter.getId() != copyTo.getId()) {
                oldStarter.setIsStarterList(false);
                shoppingListRepository.save(oldStarter);
            }
        }

        // save changed list
        copyTo.setLastUpdate(new Date());
        return shoppingListRepository.save(copyTo);
    }

    public void performItemOperation(String userName, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) {
        // get source list
        ShoppingListEntity sourceList = getListById(userName, sourceListId);

        if (sourceList == null) {
            return;
        }

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
        List<TagEntity> tagList = new ArrayList(tagDictionary.values());

        // if operation requires copy, get destinationList and copy
        if (operationType.equals(ItemOperationType.Copy) ||
                operationType.equals(ItemOperationType.Move)) {
            ShoppingListEntity targetList = getListById(userName, destinationListId);
            List<ItemEntity> items = targetList.getItems();


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


            List<ItemEntity> items = sourceList.getItems();
            CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                    .withStatisticCountType(StatisticCountType.Single)
                    .withRemoveEntireItem(true).build();
            ListItemCollector collector = createListItemCollector(sourceListId, items);
            collector.removeItemsByTagIds(tagIds, context);
            saveListChanges(sourceList, collector, context);
        }

    }

    private List<ItemEntity> itemsForTags(List<TagEntity> tagList, Long sourceListId) {
        Map tagMap = tagList.stream().collect(Collectors.toMap((t) -> t.getId(), (t) -> t.getId()));
        List<ItemEntity> listItems = itemRepository.findByListId(sourceListId);
        return listItems.stream()
                .filter(t -> t.getTag() != null)
                .filter(t -> tagMap.containsKey(t.getTag().getId())).collect(Collectors.toList());
    }

    private List<Long> getTagIdsForOperationType(ItemOperationType operationType, ShoppingListEntity sourceList) {
        if (operationType.equals(ItemOperationType.RemoveCrossedOff)) {
            return sourceList.getItems().stream()
                    .filter(item -> item.getCrossedOff() != null)
                    .map(ItemEntity::getTag)
                    .filter(Objects::nonNull)
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
        } else if (operationType.equals(ItemOperationType.RemoveAll)) {
            return sourceList.getItems().stream()
                    .map(ItemEntity::getTag)
                    .filter(Objects::nonNull)
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void addDishesToList(String userName, Long listId, ListAddProperties listAddProperties) throws ShoppingListException {
        // retrieve list
        ShoppingListEntity newList = getListById(userName, listId);
        if (newList == null) {
            throw new ObjectNotFoundException("No list found for user [" + userName + "] with list id [" + listId + "])");
        }
        ListItemCollector collector = createListItemCollector(newList.getId(), null);

        // get dishes to add
        List<Long> dishIds = listAddProperties.getDishSourceIds();
        if (dishIds.isEmpty()) {
            return;
        }

        // now, add all dish ids
        for (Long id : dishIds) {
            addDishToList(userName, collector, id);
        }

        // save changes
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(newList, collector, context);
    }


    @Override
    public ShoppingListEntity generateListForUser(String userName, ListGenerateProperties listGenerateProperties) throws ShoppingListException {
        UserEntity user = userService.getUserByUserEmail(userName);

        // check list name
        String listNameFromProperties = listGenerateProperties.getListName();
        if (listNameFromProperties == null || listNameFromProperties.isEmpty()) {
            listNameFromProperties = defaultShoppingListName;
        }
        String listName = ensureListNameIsUnique(user.getId(), listNameFromProperties);
        // create list
        ShoppingListEntity newList = createList(userName, listName);
        ListItemCollector collector = createListItemCollector(newList.getId(), null);

        // get dishes to add
        List<Long> dishIds = new ArrayList<>();
        if (listGenerateProperties.getDishSourcesIds() != null) {
            dishIds = listGenerateProperties.getDishSourcesIds();
        } else if (listGenerateProperties.getMealPlanSourceId() != null) {
            // get dishIds for meal plan
            MealPlanEntity mealPlan = mealPlanService.getMealPlanById(userName, listGenerateProperties.getMealPlanSourceId());
            dishIds = new ArrayList<>();
            if (mealPlan.getSlots() != null) {
                for (SlotEntity slot : mealPlan.getSlots()) {
                    dishIds.add(slot.getDish().getId());
                }
            }
        }

        // now, add all dish ids
        for (Long id : dishIds) {
            addDishToList(userName,collector, id);
        }

        // add starter list - if desired
        if (Boolean.TRUE.equals(listGenerateProperties.getAddFromStarter())) {
            // add Items from BaseList
            ShoppingListEntity baseList = getStarterList(userName);
            if (baseList != null) {
                CollectorContext context = new CollectorContextBuilder().create(ContextType.List)
                        .withListId(baseList.getId())
                        .withStatisticCountType(StatisticCountType.StarterList)
                        .build();
                collector.copyExistingItemsIntoList(baseList.getItems(), context);
            }
        }

        // check about generating a meal plan
        generateMealPlanOnListCreate(userName, listGenerateProperties);

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

    private void generateMealPlanOnListCreate(String userName, ListGenerateProperties listGenerateProperties) {
        if (Boolean.TRUE.equals(listGenerateProperties.getGenerateMealplan()) &&
                listGenerateProperties.getMealPlanSourceId() == null &&
                listGenerateProperties.getDishSourcesIds() != null) {
            MealPlanEntity mp = mealPlanService.createMealPlan(userName, new MealPlanEntity());
            for (Long ds : listGenerateProperties.getDishSourcesIds()) {
                mealPlanService.addDishToMealPlan(userName, mp.getId(), ds);
            }
        }
    }

    private ListLayoutEntity getListLayout(ListLayoutType listLayoutType) {
        // nothing yet for user - eventually, we could consider user preferences / properties here


        if (listLayoutType != null) {
            return listLayoutService.getListLayoutByType(listLayoutType);
        }
        listLayoutType = shoppingListProperties.getDefaultLayout();
        if (listLayoutType != null) {
            return listLayoutService.getListLayoutByType(listLayoutType);
        }

        // get layout for listtype
        return listLayoutService.getListLayoutByType(ListLayoutType.All);


    }

    @Override
    public ShoppingListEntity getStarterList(String userName) {
        UserEntity user = userService.getUserByUserEmail(userName);

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdAndIsStarterListTrue(user.getId());
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    @Override
    public ShoppingListEntity getMostRecentList(String userName) {
        UserEntity user = userService.getUserByUserEmail(userName);

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdOrderByLastUpdateDesc(user.getId());
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }


    @Override
    public ShoppingListEntity getListById(String userName, Long listId) {
        final String message = String.format("Retrieving List for id %d and name %s", listId, userName);
        logger.debug(message);
        UserEntity user = userService.getUserByUserEmail(userName);
        if (user == null) {
            return null;
        }
        Optional<ShoppingListEntity> shoppingListEntityOpt;
        shoppingListEntityOpt = shoppingListRepository.getWithItemsByListId(listId);

        // may be a list which doesn't have items.  Check for that here
        if (!shoppingListEntityOpt.isPresent()) {
            shoppingListEntityOpt = shoppingListRepository.findById(listId);
        }
        ShoppingListEntity shoppingListEntity = shoppingListEntityOpt.orElse(null);
        if (shoppingListEntity != null && shoppingListEntity.getUserId().equals(user.getId())) {
            return shoppingListEntity;
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteList(String userName, Long listId) {
        List<ShoppingListEntity> allLists = getListsByUsername(userName);
        if (allLists == null || allLists.isEmpty()) {
            throw new ActionInvalidException("No lists found for username " + userName);
        }
        if (allLists.size() < 2) {
            throw new ActionInvalidException("Can't delete the last list for username" + userName);
        }
        Optional<ShoppingListEntity> toDeleteOpt = allLists.stream()
                .filter(l -> l.getId().equals(listId)).findFirst();
        if (!toDeleteOpt.isPresent()) {
            throw new ObjectNotFoundException("Can't find list [" + listId + "] for userName [" + userName + "] to delete.");
        }

        ShoppingListEntity toDelete = toDeleteOpt.get();
        if (toDelete != null) {
            List<ItemEntity> items = itemRepository.findByListId(listId);
            itemRepository.deleteAll(items);
            toDelete.setItems(new ArrayList<>());
            shoppingListRepository.save(toDelete);
            toDelete = getListById(userName, listId);
            shoppingListRepository.delete(toDelete.getId());
            shoppingListRepository.flush();
        }
    }

    @Override
    public void addItemToListByTag(String name, Long listId, Long tagId) {
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        List<ItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        // fill in tag, if item contains tag
        TagEntity tag = null;
        tag = tagService.getTagById(tagId);
        ItemEntity item = new ItemEntity();
        item.setTag(tag);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                .withStatisticCountType(StatisticCountType.Single).build();
        collector.addItem(item, context);

        saveListChanges(shoppingListEntity, collector, context);

    }


    @Override
    public void updateItemCount(String name, Long listId, Long tagId, Integer usedCount) {
        if (usedCount == null) {
            throw new ActionInvalidException("usedCount is null in updateItemCount.");
        }

        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ItemEntity item = itemRepository.getItemByListAndTag(listId, tagId);
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
    public void deleteAllItemsFromList(String name, Long listId) {
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }
        List<ItemEntity> itemEntities = itemRepository.findByListId(listId);
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
    public void deleteItemFromList(String name, Long listId, Long itemId, Boolean removeEntireItem, Long dishSourceId) {
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }
        Optional<ItemEntity> itemEntityOpt = itemRepository.findById(itemId);
        if (!itemEntityOpt.isPresent()) {
            return;
        }
        List<ItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        ItemEntity item = itemEntityOpt.get();
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
    public ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId) {
        // get list layout by type
        ListLayoutType generalLayout = shoppingListProperties.getDefaultLayout();
        Optional<ListLayoutEntity> listLayoutEntityOptional = listLayoutService.getListLayouts()
                .stream()
                .filter(t -> t.getLayoutType().equals(generalLayout))
                .findFirst();

        if (!listLayoutEntityOptional.isPresent()) {
            return null;
        }

        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(name, mealPlanId);

        // create new inprocess list
        ShoppingListEntity savedNewList = createList(name, defaultShoppingListName);

        // add to the new list
        return addToListFromMealPlan(name, savedNewList, mealPlan);

    }

    public ShoppingListEntity addToListFromMealPlan(String name, Long listId, Long mealPlanId) {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(name, mealPlanId);

        // create new inprocess list
        ShoppingListEntity shoppingList = getListById(name, listId);

        return addToListFromMealPlan(name, shoppingList, mealPlan);
    }

    private ShoppingListEntity addToListFromMealPlan(String name, ShoppingListEntity shoppingList, MealPlanEntity mealPlan) {
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
            List<TagEntity> tags = tagService.getTagsForDish(name, slot.getDish().getId(), tagTypeList);
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

    public List<ListShopCategory> categorizeList(ShoppingListEntity shoppingListEntity) {

        if (shoppingListEntity == null) {
            return new ArrayList<>();
        }

        if (shoppingListEntity.getItems() == null || shoppingListEntity.getItems().isEmpty()) {
            return new ArrayList<>();
        }

        // get category to item dictionary (tag key, category value)
        List<TagEntity> tagList = shoppingListEntity.getItems().stream().map(ItemEntity::getTag).collect(Collectors.toList());
        Map<Long, Long> dictionary = getCategoryDictionary(shoppingListEntity.getListLayoutId(), tagList);
        if (dictionary.isEmpty()) {
            return new ArrayList<>();
        }
        // get categories for items
        List<ListLayoutCategoryEntity> categoriesEntities = listLayoutService.getListCategoriesForLayout(shoppingListEntity.getListLayoutId());
        Map<Long, ListShopCategory> filledCategories = new HashMap<>();
        categoriesEntities.forEach(ce -> {
            ItemCategoryPojo cat = (ItemCategoryPojo) new ItemCategoryPojo(ce.getName(), ce.getId(), CategoryType.Standard)
                    .displayOrder(ce.getDisplayOrder());
            filledCategories.put(cat.getId(), cat);
        });

        // find frequently crossed off
        List<Long> frequentTagIds = listTagStatisticService.findFrequentIdsForList(shoppingListEntity.getId(), shoppingListEntity.getUserId());

        // put items into categories
        for (ItemEntity item : shoppingListEntity.getItems()) {
            if (item.getRemovedOn() != null) {
                continue;
            }
            if (frequentTagIds.contains(item.getTag().getId())) {
                item.addHandle(ShoppingListService.FREQUENT);
            }

            ItemCategoryPojo category = (ItemCategoryPojo) filledCategories.get(dictionary.get(item.getTag().getId()));
            if (category != null) {
                category.addItemEntity(item);
            }
        }

        // sort items in filled categories - content sort
        for (Map.Entry entry : filledCategories.entrySet()) {
            ((ItemCategoryPojo) entry.getValue()).sortItems();
        }

        // structure categories
        listLayoutService.structureCategories(filledCategories, shoppingListEntity.getListLayoutId(), true);

        // prune and sort categories - sorts the categories themselves, not the contents
        return cleanUpResults(filledCategories);
    }

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
    @Override
    public MergeResult mergeFromClient(String userName, MergeRequest mergeRequest) {
        Long listToMergeId = mergeRequest.getListId();
        if (listToMergeId == null) {
            // oops - no list id
            throw new ObjectNotFoundException("List to merge has empty listId for user [" + userName + "]");
        }

        // get list to merge
        ShoppingListEntity list = getListById(userName, listToMergeId);

        if (list == null) {
            // oops - list isn't here any more to merge
            throw new ObjectNotFoundException("List to merge [" + listToMergeId + "] not found for user [" + userName + "]");
        }

        // create MergeCollector from list
        MergeItemCollector mergeCollector = new MergeItemCollector(list.getId(),list.getItems());
        checkReplaceTagsInCollector(mergeCollector);

        // prepare items from client
        List<ItemEntity> mergeItems = convertClientItemsToItemEntities(mergeRequest);

        // merge from client
        // fill in tags for passed items
        mergeCollector.addMergeItems(mergeItems);

        // update after merge
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Merge)
                .withStatisticCountType(StatisticCountType.Single)
                .build();
        saveListChanges(list, mergeCollector, context);

        // delete tags by removed on
        LocalDate removedBeforeDate = LocalDate.now().minusDays(mergeDeleteAfterDays);
        List<ItemEntity> itemsToRemove = itemRepository.findByRemovedOnBefore(java.sql.Date.valueOf(removedBeforeDate));
        itemRepository.deleteAll(itemsToRemove);


        return new MergeResult();
    }


    @Override
    public void addListToList(String name, Long listId, Long fromListId) {
        // get the target list
        ShoppingListEntity list = getListById(name, listId);

        // get the list to add
        ShoppingListEntity toAdd = getListById(name, fromListId);
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

    @Override
    public void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException {
        // get the list
        ShoppingListEntity list = getListById(name, listId);

        // create collector
        ListItemCollector collector = createListItemCollector(listId, list.getItems());

        addDishToList(name,collector, dishId);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(list, collector, context);
    }

    @Override
    public void fillSources(ShoppingListEntity result) {
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
            List<Long> dishIds = new ArrayList<>();
            dishIds.addAll(dishIdSet);
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
            if (sourceListIds != null && !sourceListIds.isEmpty()) {
                List<ShoppingListEntity> sourceLists = shoppingListRepository.findAllById(sourceListIds);
                // set in shopping list
                result.setListSources(sourceLists);

            }
        }
    }


    @Override
    public void changeListLayout(String name, Long listId, Long layoutId) {
        // get shopping list
        ShoppingListEntity shoppingList = getListById(name, listId);
        // set new layout id in shopping list
        shoppingList.setListLayoutId(layoutId);
        // save shopping list
        shoppingListRepository.save(shoppingList);

    }

    @Override
    public void removeDishFromList(String name, Long listId, Long dishId) {
        // get list
        ShoppingListEntity shoppingList = getListById(name, listId);

        // make collector
        ListItemCollector collector = createListItemCollector(listId, shoppingList.getItems());

        List<TagEntity> tagsToRemove = tagService.getTagsForDish(name, dishId);

        collector.removeTagsForDish(dishId, tagsToRemove);
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withRemoveEntireItem(false)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();
        saveListChanges(shoppingList, collector, context);

    }

    @Override
    public void removeListItemsFromList(String name, Long listId, Long fromListId) {
        // get list
        ShoppingListEntity shoppingList = getListById(name, listId);

        // get list to remove
        ShoppingListEntity listToRemove = getListById(name, fromListId);

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
    public void updateItemCrossedOff(String name, Long listId, Long itemId, Boolean crossedOff) {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        Optional<ItemEntity> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return;
        }
        ItemEntity item = itemOpt.get();

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
    public void crossOffAllItems(String name, Long listId, boolean crossOff) {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        List<ItemEntity> items = shoppingListEntity.getItems();

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

    private Map<Long, Long> getCategoryDictionary(Long layoutId, List<TagEntity> tagList) {
        return listSearchService.getTagToCategoryMap(layoutId, tagList);
    }

    private void checkReplaceTagsInCollector(ItemCollector mergeCollector) {
        Set<Long> allServerTagIds = new HashSet<>();
        allServerTagIds.addAll(mergeCollector.getAllTagIds());

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

    private ListItemCollector createListItemCollector(Long listId, List<ItemEntity> items) {
        ListItemCollector collector = new ListItemCollector(listId, items);
        checkReplaceTagsInCollector(collector);
        return collector;
    }

    private List<ItemEntity> convertClientItemsToItemEntities(MergeRequest mergeRequest) {
        Map<String, ItemEntity> mergeMap = mergeRequest.getMergeItems().stream()
                .filter(i -> i.getTagId() != null)
                .collect(Collectors.toMap(Item::getTagId, ModelMapper::toEntity));
        Set<Long> tagKeys = mergeMap.keySet().stream().map(Long::valueOf).collect(Collectors.toSet());

        if (tagKeys.isEmpty()) {
            return new ArrayList<>();
        }
        List<TagEntity> outdatedClientTags = tagService.getReplacedTagsFromIds(tagKeys);
        Map<Long, TagEntity> outdatedClientDictionary = new HashMap<>();
        if (!outdatedClientTags.isEmpty()) {
            Set<Long> outdatedIds = outdatedClientTags.stream().map(TagEntity::getReplacementTagId).collect(Collectors.toSet());
            outdatedClientDictionary = tagService.getDictionaryForIds(outdatedIds);
        }
        Map<Long, TagEntity> tagDictionary = tagService.getDictionaryForIds(tagKeys);

        Map<Long, ItemEntity> itemMap = new HashMap<>();
        for (Map.Entry<String, ItemEntity> entry : mergeMap.entrySet()) {
            String tagIdString = entry.getKey();
            ItemEntity item = entry.getValue();
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

    private void addItemToClientMap(ItemEntity item, Map<Long, ItemEntity> itemMap) {
        if (item.getTag() == null) {
            return;
        }
        Long tagId = item.getTag().getId();
        ItemEntity toAddTo = itemMap.get(tagId);
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

    private List<ListShopCategory> cleanUpResults(Map<Long, ListShopCategory> filledCategories) {
        List<ListShopCategory> result = new ArrayList<>();
        for (ListShopCategory cat : filledCategories.values()) {
            ItemCategoryPojo c = (ItemCategoryPojo) cat;
            if (!c.isEmpty()) {
                result.add(c);
            }
        }

        // return list of categories
        result.sort(Comparator.comparing(ListShopCategory::getDisplayOrder));
        return result;
    }

    private void addDishToList(String name, ListItemCollector collector, Long dishId) throws ShoppingListException {
        // gather tags for dish to add
        if (dishId == null) {
            logger.error("No dish found for null dishId");
            throw new ShoppingListException("No dish found for null dishId.");
        }
        List<TagEntity> tagsForDish = tagService.getTagsForDish(name, dishId).stream()
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


    private ShoppingListEntity createList(String userName, String listName) {
        UserEntity user = userService.getUserByUserEmail(userName);
        return createList(user.getId(), listName);

    }

    private ShoppingListEntity createList(Long userId, String listName) {
        ShoppingListEntity newList = new ShoppingListEntity();
        // get list layout for user, list_type
        ListLayoutEntity listLayout = getListLayout(null);
        newList.setListLayoutId(listLayout.getId());
        newList.setName(listName);
        newList.setIsStarterList(false);
        newList.setCreatedOn(new Date());
        newList.setUserId(userId);
        return shoppingListRepository.save(newList);
    }


}
