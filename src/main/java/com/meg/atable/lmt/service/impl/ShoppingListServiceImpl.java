package com.meg.atable.lmt.service.impl;

import com.meg.atable.lmt.api.model.*;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.ItemChangeRepository;
import com.meg.atable.lmt.data.repository.ItemRepository;
import com.meg.atable.lmt.data.repository.ShoppingListRepository;
import com.meg.atable.lmt.service.*;
import com.meg.atable.lmt.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return getListById(name, listIdToRetrieve);
        }
        // if the list doesn't exist, return a new list
        return createListForUser(name, ListType.ActiveList);
    }

    @Override
    public ShoppingListEntity createList(String userName, ShoppingListEntity shoppingList) {
        UserEntity user = userService.getUserByUserEmail(userName);
        // get list layout for user, list_type
        ListLayoutEntity listLayout = getListLayout(shoppingList.getListType(), null);
        shoppingList.setListLayoutId(listLayout.getId());
        shoppingList.setCreatedOn(new Date());
        shoppingList.setUserId(user.getId());
        return shoppingListRepository.save(shoppingList);
    }

    private ShoppingListEntity createListForUser(String userName, ListType listType) {
        UserEntity user = userService.getUserByUserEmail(userName);
        ShoppingListEntity newList = new ShoppingListEntity();
        newList.setListType(listType != null ? listType : ListType.General);
        // get list layout for user, list_type
        ListLayoutEntity listLayout = getListLayout(listType, null);
        newList.setListLayoutId(listLayout.getId());
        newList.setCreatedOn(new Date());
        newList.setUserId(user.getId());
        return shoppingListRepository.save(newList);
    }

    @Override
    public ShoppingListEntity createList(String userName, ListGenerateProperties listGenerateProperties) throws ShoppingListException {
        // create list
        ShoppingListEntity newList = createListForUser(userName, listGenerateProperties.getListType());
        ListItemCollector collector = new ListItemCollector(newList.getId(), null);

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

        // add base list - if desired
        if (listGenerateProperties.getAddFromBase()) {
            // add Items from BaseList
            ShoppingListEntity baseList = getListByUsernameAndType(userName, ListType.BaseList);
            if (baseList != null) {
                collector.copyExistingItemsIntoList(ItemSourceType.BaseList.name(), baseList.getItems(), false);
            }
        }

        // add pickup list - if desired
        // add base list - if desired
        if (listGenerateProperties.getAddFromBase()) {
            // add Items from BaseList
            ShoppingListEntity baseList = getListByUsernameAndType(userName, ListType.PickUpList);
            if (baseList != null) {
                collector.copyExistingItemsIntoList(ItemSourceType.PickUpList.name(), baseList.getItems(), true);
            }
        }

        // check about generating a meal plan
        if (listGenerateProperties.getGenerateMealplan() &&
                listGenerateProperties.getMealPlanSourceId() == null &&
                listGenerateProperties.getDishSourcesIds() != null) {
            MealPlanEntity mp = mealPlanService.createMealPlan(userName, new MealPlanEntity());
            for (Long ds : listGenerateProperties.getDishSourcesIds()) {
                mealPlanService.addDishToMealPlan(userName, mp.getId(), ds);
            }
            // TODO add link to shopping list here
        }

        // save changes
        saveListChanges(newList, collector);
        return newList;

    }


    private ListLayoutEntity getListLayout(ListType listType, ListLayoutType listLayoutType) {
        // nothing yet for user - eventually, we could consider user preferences / properties here

        ListLayoutType resultlayout = listLayoutType;
        if (resultlayout != null) {
            return listLayoutService.getListLayoutByType(resultlayout);
        }
            resultlayout = shoppingListProperties.getDefaultLayouts().get(listType);
        if (resultlayout != null) {
            return listLayoutService.getListLayoutByType(resultlayout);
        }

        resultlayout = shoppingListProperties.getDefaultListLayoutType();
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
        ListItemCollector collector = new ListItemCollector(listId, items);
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
        ListItemCollector collector = new ListItemCollector(listId, items);
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
        ListLayoutType generalLayout = shoppingListProperties.getDefaultLayouts().get(ListType.General);
        Optional<ListLayoutEntity> listLayoutEntityOptional = listLayoutService.getListLayouts()
                .stream()
                .filter(t -> t.getLayoutType().equals(generalLayout))
                .findFirst();

        if (!listLayoutEntityOptional.isPresent()) {
            return null;
        }

        ListLayoutEntity listLayoutEntity = listLayoutEntityOptional.get();

        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(name, mealPlanId);

        // create new inprocess list
        ShoppingListEntity newList = new ShoppingListEntity();
        newList.setListLayoutId(listLayoutEntity.getId());
        newList.setListType(ListType.General);
        ShoppingListEntity savedNewList = createList(name, newList);
        // create the collector
        ListItemCollector collector = new ListItemCollector(savedNewList.getId());


        // add all tags for meal plan
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);
        for (SlotEntity slot : mealPlan.getSlots()) {
            List<TagEntity> tags = tagService.getTagsForDish(name, slot.getDish().getId(), tagTypeList);
            collector.addTags(tags, slot.getDish().getId(), null);
        }

        // add Items from BaseList
        ShoppingListEntity baseList = getListByUsernameAndType(name, ListType.BaseList);
        if (baseList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.BaseList.name(), baseList.getItems(), false);
        }

        // add Items from PickUpList
        ShoppingListEntity pickupList = getListByUsernameAndType(name, ListType.PickUpList);
        if (pickupList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.PickUpList.name(), pickupList.getItems(), true);
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        saveListChanges(newList, collector);
        Optional<ShoppingListEntity> shoppingListEntity = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(newList.getId());
        return shoppingListEntity.orElse(null);

    }

    @Transactional
    public ShoppingListEntity setListActive(String username, Long listId, GenerateType generateType) {
        UserEntity user = userService.getUserByUserEmail(username);
        // get active list
        ShoppingListEntity oldActive = shoppingListRepository.findWithItemsByUserIdAndListType(user.getId(), ListType.ActiveList);
        // get list to set active
        ShoppingListEntity toActive = getListById(username, listId);

        // add list to collector
        ListItemCollector collector = new ListItemCollector(toActive.getId(), toActive.getItems());

        // if generatetype add, add items to current list
        if (generateType == GenerateType.Add && oldActive != null) {
            collector.copyExistingItemsIntoList(null, oldActive.getItems(), false);
        }

        // delete old active list
        if (oldActive != null) {
            deleteList(username, oldActive.getId());
        }
        // set current list active
        toActive.setListType(ListType.ActiveList);
        // save active list
        saveListChanges(toActive, collector);
        return getListById(username, listId);

    }

    public List<Category> categorizeList(String userName, ShoppingListEntity shoppingListEntity, Long highlightDishId, Boolean showPantry, ListType highlightListType) {
        boolean isHighlightDish = highlightDishId != null && !highlightDishId.equals(0L);
        boolean isHighlightList = !isHighlightDish && highlightListType != null;
        boolean separateFrequent = showPantry != null && showPantry;

        String highlightName = getHighlightDishName(userName, isHighlightDish, highlightDishId);
        Set<Long> dishItemIds = getHighlightDishItemIds(isHighlightDish, shoppingListEntity, highlightDishId);
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


        ItemCategory frequent = createDefaultCategoryByType(CategoryType.Frequent,null);
        ItemCategory uncategorized = createDefaultCategoryByType(CategoryType.UnCategorized,null);
        ItemCategory highlight = createDefaultCategoryByType(CategoryType.Highlight, highlightName);
        ItemCategory highlightList = createDefaultCategoryByType(CategoryType.HighlightList,null);
        for (ItemEntity item : shoppingListEntity.getItems()) {
            if (item.isFrequent() && separateFrequent && !isHighlightDish) {
                frequent.addItemEntity(item);
            } else if (!dictionary.containsKey(item.getTag().getId())) {
                uncategorized.addItemEntity(item);
            } else if (isHighlightDish && dishItemIds.contains(item.getId())) {
                highlight.addItemEntity(item);
            } else if (isHighlightList && item.getRawListSources().contains(highlightListType.name())) {
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

        // sort items in filled categories
        for (Map.Entry entry : filledCategories.entrySet()) {
            ((ItemCategory) entry.getValue()).sortItems();
        }

        // structure categories
        listLayoutService.structureCategories(filledCategories, shoppingListEntity.getListLayoutId(), true);

        // add frequent and uncategorized
        addCategoryIfNotEmpty(filledCategories,frequent);
        addCategoryIfNotEmpty(filledCategories,uncategorized);
        addCategoryIfNotEmpty(filledCategories,highlight);
        addCategoryIfNotEmpty(filledCategories,highlightList);

        // prune and sort categories
        return cleanUpResults(filledCategories);
    }

    private ItemCategory createDefaultCategoryByType(CategoryType categoryType, String highlightName) {
        String categoryName = shoppingListProperties.getCategoryNameByType(categoryType);
        if (categoryName == null) {
            categoryName = highlightName;
        }
        Long idAndSort = shoppingListProperties.getIdAndSortByType(categoryType);
        Integer idAndSortInt = idAndSort.intValue();

        return new ItemCategory(   categoryName, idAndSort,idAndSortInt,categoryType);

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
        if (!category.isEmpty()) {
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

    private String getHighlightDishName(String userName, boolean isHighlightDish, Long highlightDishId)  {
        if (!isHighlightDish || highlightDishId == null) {
            return "";
        }

        DishEntity dish = dishService.getDishForUserById(userName,highlightDishId);

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
        ListItemCollector collector = new ListItemCollector(listId, list.getItems());

        // add Items from PickUpList
        boolean incrementStats = listType != ListType.BaseList;
        collector.copyExistingItemsIntoList(listType.name(), toAdd.getItems(), incrementStats);

        // save list
        saveListChanges(list, collector);
    }

    private void addDishToList(String name,ListItemCollector collector, Long dishId) throws ShoppingListException {
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
        collector.addTags(tagsForDish, dishId, null);

        // update last added date for dish
        this.dishService.updateLastAddedForDish(dishId);
    }

    @Override
    public void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException {
        // get the list
        ShoppingListEntity list = getListById(name, listId);

        // create collector
        ListItemCollector collector = new ListItemCollector(listId, list.getItems());

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
            List<String> listSources = new ArrayList<>();
            listSources.addAll(listSourceSet);
            // set in shopping list
            result.setListSources(listSources);
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
        ListItemCollector collector = new ListItemCollector(listId, shoppingList.getItems());

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
        ListItemCollector collector = new ListItemCollector(listId, shoppingList.getItems());

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
            item.setCrossedOff(new Date());
        } else {
            item.setCrossedOff(null);
        }

        itemRepository.save(item);

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

        itemRepository.saveAll(items);
    }

    private void saveListChanges(ShoppingListEntity shoppingList, ListItemCollector collector) {
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

}
