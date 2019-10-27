package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.DateUtils;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.common.StringTools;
import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.ItemChangeRepository;
import com.meg.atable.lmt.data.repository.ItemRepository;
import com.meg.atable.lmt.data.repository.ShoppingListRepository;
import com.meg.atable.lmt.service.*;
import com.meg.atable.lmt.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
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
    ItemChangeRepository itemChangeRepository;

    private final ShoppingListProperties shoppingListProperties;

    @Value("${service.shoppinglistservice.merge.items.deleted.after.days}")
    int mergeDeleteAfterDays = 6;

    @Value("${service.shoppinglistservice.default.list.name}")
    String defaultShoppingListName;

    @Autowired
    public ShoppingListServiceImpl(UserService userService, TagService tagService, DishService dishService, ShoppingListRepository shoppingListRepository, ListLayoutService listLayoutService, ListSearchService listSearchService, MealPlanService mealPlanService, ItemRepository itemRepository, ItemChangeRepository itemChangeRepository, ShoppingListProperties shoppingListProperties) {
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
    }

    @Override
    public List<ShoppingListEntity> getListsByUsername(String userName) {
        UserEntity user = userService.getUserByUserEmail(userName);

        return shoppingListRepository.findByUserId(user.getId());
    }

    @Override
    public ShoppingListEntity getActiveListForUser(String name) {
        return getActiveListForUser(name, false);
    }

    @Override
    public List<ItemEntity> getChangedItemsForActiveList(String name, Date changedAfter, Long layoutId) {
        ShoppingListEntity shoppingListEntity = getActiveListForUser(name);

        return itemRepository.getItemsChangedAfter(changedAfter, shoppingListEntity.getId());
    }

    @Override
    public ShoppingListEntity getActiveListForUser(String name, boolean includeRemoved) {
        UserEntity user = userService.getUserByUserEmail(name);

        // get all lists
        List<ShoppingListEntity> userLists = shoppingListRepository.findByUserId(user.getId());

        // put them in a map
        Map<String, List<ShoppingListEntity>> listMap = new HashMap<>();
        for (ShoppingListEntity list : userLists) {
            String type = list.getListType() != null ? list.getListType().name() : "no_type";
            if (!listMap.containsKey(type)) {
                listMap.put(type,new ArrayList<>());
            }
            listMap.get(type).add(list);
        }

        Long listIdToRetrieve = null;
        // find list to retrieve
        List<String> orderedTypes = Arrays.asList(ListType.ActiveList.name(), ListType.General.name(), "no_type");
        for (String key : orderedTypes) {
            if (listMap.containsKey(key)) {
                listIdToRetrieve = listMap.get(key).get(0).getId();
                break;
            }
        }

        // if the list id exists, retreive and return the list belonging to it
        if (listIdToRetrieve != null) {
            return getListById(name, listIdToRetrieve, includeRemoved);
        }
        // if the list doesn't exist, return a new list
        return createList(name, defaultShoppingListName);
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
            if (oldStarter != null) {
                oldStarter.setIsStarterList(false);
                shoppingListRepository.save(oldStarter);
            }
        }

        // save changed list
        return shoppingListRepository.save(copyTo);
    }

    @Override
    public ShoppingListEntity generateListForUser(String userName, ListGenerateProperties listGenerateProperties) throws ShoppingListException {
        UserEntity user = userService.getUserByUserEmail(userName);

        // check list name
        String listNameFromProperties = listGenerateProperties.getListName();
        if (listNameFromProperties == null) {
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
        if (listGenerateProperties.getAddFromBase()) {
            // add Items from BaseList
            ShoppingListEntity baseList = getStarterList(userName);
            if (baseList != null) {
                collector.copyExistingItemsIntoList(String.valueOf(baseList.getId()), baseList.getItems(), false);
            }
        }

        // check about generating a meal plan
        generateMealPlanOnListCreate(userName, listGenerateProperties);

        // save changes
        saveListChanges(newList, collector);
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
        if (listGenerateProperties.getGenerateMealplan() &&
                listGenerateProperties.getMealPlanSourceId() == null &&
                listGenerateProperties.getDishSourcesIds() != null) {
            MealPlanEntity mp = mealPlanService.createMealPlan(userName, new MealPlanEntity());
            for (Long ds : listGenerateProperties.getDishSourcesIds()) {
                mealPlanService.addDishToMealPlan(userName, mp.getId(), ds);
            }
            // TODO add link to shopping list here
        }
    }

    private ListLayoutEntity getListLayout(ListLayoutType listLayoutType) {
        // nothing yet for user - eventually, we could consider user preferences / properties here

        ListLayoutType resultlayout = listLayoutType;
        if (resultlayout != null) {
            return listLayoutService.getListLayoutByType(resultlayout);
        }
        resultlayout = shoppingListProperties.getDefaultLayout();
        if (resultlayout != null) {
            return listLayoutService.getListLayoutByType(resultlayout);
        }

        // get layout for listtype
        return listLayoutService.getListLayoutByType(ListLayoutType.All);


    }

    @Override
    public ShoppingListEntity getListByUsernameAndType(String userName, ListType listType) {
        UserEntity user = userService.getUserByUserEmail(userName);

        return shoppingListRepository.findWithItemsByUserIdAndListType(user.getId(), listType);
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

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdOrderByCreatedOnDesc(user.getId());
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    @Override
    public ShoppingListEntity getListById(String userName, Long listId) {
        return getListById(userName, listId, false);
    }


    @Override
    public ShoppingListEntity getListById(String userName, Long listId, boolean includeRemoved) {
        logger.debug("Retrieving List for id [" + listId + "] and name [" + userName + "]");
        UserEntity user = userService.getUserByUserEmail(userName);
        if (user == null) {
            return null;
        }
        Optional<ShoppingListEntity> shoppingListEntityOpt;
        if (includeRemoved) {
            shoppingListEntityOpt = shoppingListRepository.getWithItemsByListId(listId);
        } else {
            shoppingListEntityOpt = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId);
        }
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
    public boolean deleteList(String userName, Long listId) {
        ShoppingListEntity toDelete = getListById(userName, listId);
        if (toDelete != null) {
            List<ItemEntity> items = toDelete.getItems();
            itemRepository.deleteAll(items);
            toDelete.setItems(new ArrayList<>());
            shoppingListRepository.delete(toDelete);
            return true;
        }
        return false;
    }

    @Override
    public void addItemToList(String name, Long listId, ItemEntity itemEntity) {
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        List<ItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = createListItemCollector(listId, items);
        // fill in tag, if item contains tag
        TagEntity tag = null;
        if (itemEntity.getTagId() != null) {
            tag = tagService.getTagById(itemEntity.getTagId());
            itemEntity.setTag(tag);
        }
        if (tag == null && itemEntity.getTag().getId() != null) {
            tag = tagService.getTagById(itemEntity.getTag().getId());
            itemEntity.setTag(tag);
        }
        // TODO need list type to itemsource translation
        collector.addItem(itemEntity);

        saveListChanges(shoppingListEntity, collector);
    }

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

        collector.addItem(item);

        saveListChanges(shoppingListEntity, collector);

    }

    private ListItemCollector createListItemCollector(Long listId, List<ItemEntity> items) {
        ListItemCollector collector = new ListItemCollector(listId, items);
        checkReplaceTagsInCollector(collector);
        return collector;
    }

    private ListItemCollector createListItemCollector(Long listId) {
        return new ListItemCollector(listId);
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

        itemRepository.deleteAll(itemEntities);
        shoppingListEntity.setItems(null);
        shoppingListEntity.setLastUpdate(new Date());
        shoppingListRepository.save(shoppingListEntity);
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

        collector.removeItemByTagId(item.getTag().getId(), dishSourceId, removeEntireItem);

        saveListChanges(shoppingListEntity, collector);
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
        // create the collector
        ListItemCollector collector = createListItemCollector(savedNewList.getId());


        // add all tags for meal plan
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);
        for (SlotEntity slot : mealPlan.getSlots()) {
            List<TagEntity> tags = tagService.getTagsForDish(name, slot.getDish().getId(), tagTypeList);
            collector.addTags(tags, slot.getDish().getId());
        }

        // add Items from BaseList
        ShoppingListEntity baseList = getListByUsernameAndType(name, ListType.BaseList);
        if (baseList != null) {
            collector.copyExistingItemsIntoList(String.valueOf(baseList.getId()), baseList.getItems(), false);
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        saveListChanges(savedNewList, collector);
        Optional<ShoppingListEntity> shoppingListEntity = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(savedNewList.getId());
        return shoppingListEntity.orElse(null);

    }

    public List<Category> categorizeList(String userName, ShoppingListEntity shoppingListEntity, Long highlightDishId,
                                         Boolean showPantry, Long highlightListId) {


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

        // fill categories for items (into hash)
        Map<Long, Category> filledCategories = new HashMap<>();
        categoriesEntities.forEach(ce -> {
            ItemCategory cat = (ItemCategory) new ItemCategory(ce.getName(), ce.getId(), CategoryType.Standard)
                    .displayOrder(ce.getDisplayOrder());
            filledCategories.put(cat.getId(), cat);
        });

        // split out items into special categories
        Map<CategoryType, ItemCategory> specialCategories = generateSpecialCategories(userName,shoppingListEntity,
                filledCategories,
                dictionary,
                highlightDishId,
                showPantry,
                highlightListId);


        // sort items in filled categories
        for (Map.Entry entry : filledCategories.entrySet()) {
            ((ItemCategory) entry.getValue()).sortItems();
        }

        // structure categories
        listLayoutService.structureCategories(filledCategories, shoppingListEntity.getListLayoutId(), true);

        // add frequent and uncategorized
        addCategoryIfNotEmpty(filledCategories,specialCategories.get(CategoryType.Frequent));
        addCategoryIfNotEmpty(filledCategories,specialCategories.get(CategoryType.UnCategorized));
        addCategoryIfNotEmpty(filledCategories,specialCategories.get(CategoryType.Highlight));
        addCategoryIfNotEmpty(filledCategories,specialCategories.get(CategoryType.HighlightList));

        // prune and sort categories
        return cleanUpResults(filledCategories);
    }

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
    @Override
    public MergeResult mergeFromClient(String userName, MergeRequest mergeRequest) {

        // get active list for user
        ShoppingListEntity list = getActiveListForUser(userName, true);

        // ensure active list id equals mergeList
        if (!list.getId().equals(mergeRequest.getListId())) {
            logger.error("Trying to merge list which is not currently active! username [" + userName + "], active_id [" + list.getId() + "], merge_list_id [" + mergeRequest.getListId() + "]");
        }
        // create MergeCollector from list
        MergeItemCollector mergeCollector = new MergeItemCollector(list.getId(),list.getItems());

        // prepare items from client
        List<ItemEntity> mergeItems = convertClientItemsToItemEntities(mergeRequest);

        // swap out tags which have been replaced - server
        // replace any tags which need to be replaced
        checkReplaceTagsInCollector(mergeCollector);

        // merge from client
        // fill in tags for passed items
        mergeCollector.addMergeItems(mergeItems);

        // update after merge
        saveListChanges(list, mergeCollector);

        // delete tags by removed on
        LocalDate removedBeforeDate = LocalDate.now().minusDays(mergeDeleteAfterDays);
        List<ItemEntity> itemsToRemove = itemRepository.findByRemovedOnBefore(java.sql.Date.valueOf(removedBeforeDate));
        itemRepository.deleteAll(itemsToRemove);


        return new MergeResult();
    }

    //MM come back to this
    @Override
    public void addListToList(String name, Long listId, ListType listType) {
        // get the target list
        ShoppingListEntity list = getListById(name, listId);

        // get the list to add
        ShoppingListEntity toAdd = getListByUsernameAndType(name, listType);
        if (toAdd == null) {
            return;
        }

        // create collector
        ListItemCollector collector = createListItemCollector(listId, list.getItems());

        // add Items from PickUpList
        boolean incrementStats = listType != ListType.BaseList; //MM starter list
        collector.copyExistingItemsIntoList(listType.name(), toAdd.getItems(), incrementStats);

        // save list
        saveListChanges(list, collector);
    }

    @Override
    public void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException {
        // get the list
        ShoppingListEntity list = getListById(name, listId);

        // create collector
        ListItemCollector collector = createListItemCollector(listId, list.getItems());

        addDishToList(name,collector, dishId);

        saveListChanges(list, collector);
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
            List<Long> sourceListIds = listSourceSet.stream().map(stringval -> Long.valueOf(stringval)).collect(Collectors.toList());
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

        saveListChanges(shoppingList, collector);

    }

    @Override
    public void removeListItemsFromList(String name, Long listId, ListType listType) {
        // get list
        ShoppingListEntity shoppingList = getListById(name, listId);

        // get list to remove
        ShoppingListEntity toRemove = getListByUsernameAndType(name, listType);

        // make collector
        ListItemCollector collector = createListItemCollector(listId, shoppingList.getItems());

        collector.removeItemsFromList(listType, toRemove.getItems());

        saveListChanges(shoppingList, collector);
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
        if (!item.getListId().equals(shoppingListEntity.getId())) {
            return;
        }

        // set crossed off for item - by setting crossedOff date
        if (crossedOff) {
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

        items.forEach(i -> i.setCrossedOff(crossOffDate));

        if (crossOff) {
            shoppingListEntity.setLastUpdate(new Date());
        }
        itemRepository.saveAll(items);
    }

    private void saveListChanges(ShoppingListEntity shoppingList, ItemCollector collector) {
        itemChangeRepository.saveItemChanges(collector, shoppingList.getUserId());
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

    private List<ItemEntity> convertClientItemsToItemEntities(MergeRequest mergeRequest) {
        Map<String, ItemEntity> mergeMap = mergeRequest.getMergeItems().stream()
                .filter(i -> i.getTagId() != null)
                .collect(Collectors.toMap(Item::getTagId, ModelMapper::toEntity));
        Set<Long> tagKeys = mergeMap.keySet().stream().map(k -> Long.valueOf(k)).collect(Collectors.toSet());
        mergeMap.keySet().forEach(k -> logger.debug("the  List for key [" + k + "] and item [" + mergeMap.get(k).getCrossedOff() + "]"));


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

    private Map<CategoryType, ItemCategory> generateSpecialCategories(String userName, ShoppingListEntity shoppingListEntity,
                                                                      Map<Long, Category> filledCategories,
                                                                      Map<Long, Long> dictionary,
                                                                      Long highlightDishId, Boolean showPantry,
                                                                      Long highlightListId) {
        boolean isHighlightDish = highlightDishId != null && !highlightDishId.equals(0L);
        boolean isHighlightList = !isHighlightDish && highlightListId != null;
        boolean separateFrequent = showPantry != null && showPantry;
        String highlightName = getHighlightDishName(userName, isHighlightDish, highlightDishId);
        Set<Long> dishItemIds = getHighlightDishItemIds(isHighlightDish, shoppingListEntity, highlightDishId);


        HashMap<CategoryType, ItemCategory> specialCategories = new HashMap<>();
        ItemCategory frequent = createDefaultCategoryByType(CategoryType.Frequent, null);
        ItemCategory uncategorized = createDefaultCategoryByType(CategoryType.UnCategorized, null);
        ItemCategory highlight = createDefaultCategoryByType(CategoryType.Highlight, highlightName);
        ItemCategory highlightList = createDefaultCategoryByType(CategoryType.HighlightList, null);
        for (ItemEntity item : shoppingListEntity.getItems()) {
            if (item.isFrequent() && separateFrequent && !isHighlightDish) {
                frequent.addItemEntity(item);
            } else if (!dictionary.containsKey(item.getTag().getId())) {
                uncategorized.addItemEntity(item);
            } else if (isHighlightDish && dishItemIds.contains(item.getId())) {
                highlight.addItemEntity(item);
            } else if (isHighlightList && item.getRawListSources().contains(String.valueOf(highlightListId))) {
                highlightList.addItemEntity(item);
            } else {

                ItemCategory category = (ItemCategory) filledCategories.get(dictionary.get(item.getTag().getId()));
                if (category == null) {
                    uncategorized.addItemEntity(item);
                } else {
                    category.addItemEntity(item);
                }

            }
        }

        specialCategories.put(CategoryType.Highlight, highlight);
        specialCategories.put(CategoryType.HighlightList, highlightList);
        specialCategories.put(CategoryType.Frequent, frequent);
        specialCategories.put(CategoryType.UnCategorized, uncategorized);

        return specialCategories;
    }

    private ItemCategory createDefaultCategoryByType(CategoryType categoryType, String highlightName) {
        String categoryName = shoppingListProperties.getCategoryNameByType(categoryType);
        if (categoryName == null) {
            categoryName = highlightName;
        }
        Long idAndSort = shoppingListProperties.getIdAndSortByType(categoryType);
        Integer idAndSortInt = idAndSort.intValue();

        return new ItemCategory(categoryName, idAndSort, idAndSortInt, categoryType);

    }

    private List<Category> cleanUpResults(Map<Long, Category> filledCategories) {
        List<Category> result = new ArrayList<>();
        for (Category cat : filledCategories.values()) {
            ItemCategory c = (ItemCategory) cat;
            if (!c.isEmpty()) {
                result.add(c);
            }
        }

        // return list of categories
        result.sort(Comparator.comparing(Category::getDisplayOrder));
        return result;
    }

    private void addCategoryIfNotEmpty(Map<Long, Category> filledCategories, ItemCategory category) {
        if (category != null && !category.isEmpty()) {
            filledCategories.put(category.getId(), category);
        }
    }

    private Set<Long> getHighlightDishItemIds(boolean isHighlightDish, ShoppingListEntity shoppingListEntity, Long highlightDishId) {
        Set<Long> results = new HashSet<>();

        if (!isHighlightDish || highlightDishId == null) {
            return results;
        }
        return getDishItemIds(shoppingListEntity, highlightDishId);

    }

    private String getHighlightDishName(String userName, boolean isHighlightDish, Long highlightDishId) {
        if (!isHighlightDish || highlightDishId == null) {
            return "";
        }

        DishEntity dish = dishService.getDishForUserById(userName, highlightDishId);

        return dish.getDishName();

    }

    private Set<Long> getDishItemIds(ShoppingListEntity shoppingListEntity, Long dishId) {
        Set<Long> dishItemIds = new HashSet<>();
        List<ItemEntity> items = itemRepository.getItemsForDish(shoppingListEntity.getId(), dishId);
        if (items == null || items.isEmpty()) {
            return dishItemIds;
        }
        dishItemIds = items.stream().map(ItemEntity::getId).collect(Collectors.toSet());
        return dishItemIds;
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
        if (tagsForDish == null || tagsForDish.isEmpty()) {
            logger.info("No tags found for dishId [" + dishId + "]");
            return;
        }


        // add new dish tags to list
        collector.addTags(tagsForDish, dishId);

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
