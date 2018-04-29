package com.meg.atable.service.impl;

import com.meg.atable.api.model.*;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.ItemChangeRepository;
import com.meg.atable.data.repository.ItemRepository;
import com.meg.atable.data.repository.ShoppingListRepository;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagService;
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
    public final static String uncategorized = "nocat";
    // MM need to handle this in application settings /user settings
    private final static ListLayoutType listlayoutdefault = ListLayoutType.RoughGrained;
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
        UserAccountEntity user = userService.getUserByUserName(userName);

        return shoppingListRepository.findByUserId(user.getId());
    }

    @Override
    public ShoppingListEntity createList(String userName, ShoppingListEntity shoppingList) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        // get list layout for user, list_type
        ListLayoutEntity listLayout = getListLayout(user, shoppingList.getListType(), shoppingList.getListLayoutType());
        shoppingList.setListLayoutId(listLayout.getId());
        shoppingList.setCreatedOn(new Date());
        shoppingList.setUserId(user.getId());
        return shoppingListRepository.save(shoppingList);
    }

    private ListLayoutEntity getListLayout(UserAccountEntity user, ListType listType, ListLayoutType listLayoutType) {
        // nothing yet for user - eventually, we could consider user preferences / properties here

        ListLayoutType resultlayout = listlayoutdefault;
        if (listLayoutType != null) {
            resultlayout = listLayoutType; // defaults to requested listLayoutType
        } else if (listType != null) {
            resultlayout = shoppingListProperties.getDefaultLayouts().get(listType);
        }

        // get layout for listtype
        return listLayoutService.getListLayoutByType(resultlayout);


    }

    @Override
    public ShoppingListEntity getListByUsernameAndType(String userName, ListType listType) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        return shoppingListRepository.findByUserIdAndListType(user.getId(), listType);
    }

    @Override
    public ShoppingListEntity getListById(String userName, Long listId) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        if (user == null) {
            return null;
        }
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findOne(listId);
        if (shoppingListEntity != null && shoppingListEntity.getUserId().equals(user.getId())) {
            return shoppingListEntity;
        }
        return null;
    }

    @Override
    public boolean deleteList(String userName, Long listId) {
        ShoppingListEntity toDelete = getListById(userName, listId);
        if (toDelete != null) {
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
        collector.addItem(itemEntity); // MM need list type to itemsource translation

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

        itemRepository.delete(itemEntities);
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
        ItemEntity itemEntity = itemRepository.findOne(itemId);
        if (itemEntity == null) {
            return;
        }

        List<ItemEntity> items = shoppingListEntity.getItems();
        ListItemCollector collector = new ListItemCollector(listId, items);

        if (itemEntity.getTag() == null) {
            //MM
            collector.removeFreeTextItem(itemEntity);
        }

        collector.removeItemByTagId(itemEntity.getTag().getId(), dishSourceId, removeEntireItem);

        saveListChanges(shoppingListEntity, collector);
    }

    @Override
    public ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId) {
        UserAccountEntity user = userService.getUserByUserName(name);
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
        if (mealPlan == null) {
            return null;
        }

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
            List<TagEntity> tags = tagService.getTagsForDish(slot.getDish().getId(), tagTypeList);
            collector.addTags(tags, slot.getDish().getId(), null);
        }

        // add Items from BaseList
        ShoppingListEntity baseList = getListByUsernameAndType(name, ListType.BaseList);
        if (baseList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.BaseList.name(), baseList.getItems(), true);
        }

        // add Items from PickUpList
        ShoppingListEntity pickupList = getListByUsernameAndType(name, ListType.PickUpList);
        if (pickupList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.PickUpList.name(), pickupList.getItems(), true);
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        saveListChanges(newList, collector);
        return shoppingListRepository.findOne(newList.getId());

    }

    @Transactional
    public ShoppingListEntity setListActive(String username, Long listId, GenerateType generateType) {
        UserAccountEntity user = userService.getUserByUserName(username);
        // get active list
        ShoppingListEntity oldActive = shoppingListRepository.findByUserIdAndListType(user.getId(), ListType.ActiveList);
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
        saveListChanges(toActive,collector);
        return getListById(username,listId);

    }

    public List<Category> categorizeList(ShoppingListEntity shoppingListEntity, Long highlightDishId, Boolean showPantry, ListType highlightListType) {
        boolean isHighlightDish = highlightDishId != null && !highlightDishId.equals(0L);
        boolean isHighlightList = !isHighlightDish && highlightListType != null;
        boolean separateFrequent = showPantry != null && showPantry;//shoppingListEntity.getListType().equals(ListType.InProcess);

        String highlightName = getHighlightDishName(isHighlightDish, highlightDishId);
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
        ItemCategory frequent = (ItemCategory) new ItemCategory(shoppingListProperties.getFrequentCategoryName(),
                shoppingListProperties.getFrequentIdAndSortAsLong(), CategoryType.Frequent)
                .displayOrder(shoppingListProperties.getFrequentIdAndSort());
        ItemCategory uncategorized = (ItemCategory) new ItemCategory(shoppingListProperties.getUncategorizedCategoryName(),
                shoppingListProperties.getUncategorizedIdAndSortAsLong(), CategoryType.UnCategorized)
                .displayOrder(shoppingListProperties.getUncategorizedIdAndSort());
        ItemCategory highlight = (ItemCategory) new ItemCategory(highlightName,
                shoppingListProperties.getHighlightIdAndSortAsLong(), CategoryType.Highlight)
                .displayOrder(shoppingListProperties.getHighlightIdAndSort());
        ItemCategory highlightList = (ItemCategory) new ItemCategory(highlightListType != null ? highlightListType.name() : "",
                shoppingListProperties.getHighlightListIdAndSortAsLong(), CategoryType.Highlight)
                .displayOrder(shoppingListProperties.getHighlightListIdAndSort());
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
        if (!frequent.isEmpty()) {
            filledCategories.put(frequent.getId(), frequent);
        }
        if (!uncategorized.isEmpty()) {
            filledCategories.put(uncategorized.getId(), uncategorized);
        }
        if (!highlight.isEmpty()) {
            filledCategories.put(highlight.getId(), highlight);
        }
        if (!highlightList.isEmpty()) {
            filledCategories.put(highlightList.getId(), highlightList);
        }

        // prune categories
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

    private Set<Long> getHighlightDishItemIds(boolean isHighlightDish, ShoppingListEntity shoppingListEntity, Long highlightDishId) {
        Set<Long> results = new HashSet<>();

        if (!isHighlightDish || highlightDishId == null) {
            return results;
        }
        return getDishItemIds(shoppingListEntity, highlightDishId);

    }

    private String getHighlightDishName(boolean isHighlightDish, Long highlightDishId) {
        if (!isHighlightDish || highlightDishId == null) {
            return "";
        }

        Optional<DishEntity> dishOpt = dishService.getDishById(highlightDishId);
        if (!dishOpt.isPresent()) {
            return "";
        }

        DishEntity dish = dishOpt.get();
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

    @Override
    public void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException {
        // MM experimental - not adding categories.  These won't be persisted, but
        // instead will be determined on the fly upon display

        // get the list
        ShoppingListEntity list = getListById(name, listId);

        // gather tags for dish to add
        if (dishId == null) {
            // MM TODO LOGGING HERE
            throw new ShoppingListException("No dish found for null dishId.");
        }
        List<TagEntity> tagsForDish = tagService.getTagsForDish(dishId).stream()
                .filter(t -> t.getTagType().equals(TagType.Ingredient) || t.getTagType().equals(TagType.NonEdible))
                .collect(Collectors.toList());
        if (tagsForDish == null || tagsForDish.isEmpty()) {
            throw new ShoppingListException("No tags found for dishId [" + dishId + "]");
        }

        // create collector
        ListItemCollector collector = new ListItemCollector(listId, list.getItems());

        // add new dish tags to list
        collector.addTags(tagsForDish, dishId, null);

        // update last added date for dish
        saveListChanges(list, collector);
        return;
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
        return;
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

        List<TagEntity> tagsToRemove = tagService.getTagsForDish(dishId);

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

    private void saveListChanges(ShoppingListEntity shoppingList, ListItemCollector collector) {
        itemChangeRepository.saveItemChanges(collector, shoppingList.getUserId());
        // make changes in list object
        shoppingList.setItems(collector.getAllItems());
        shoppingList.setLastUpdate(new Date());
        shoppingListRepository.save(shoppingList);
    }


    private void deleteAllItemsFromList(Long listId) {
        shoppingListRepository.bulkDeleteItemsFromList(listId);
    }

    private Map<Long, Long> getCategoryDictionary(Long layoutId, List<TagEntity> tagList) {
        return listSearchService.getTagToCategoryMap(layoutId, tagList);
    }

    private Long getCategoryIdForItem(ShoppingListEntity shoppingList, ItemEntity itemEntity) {
        if (itemEntity == null || itemEntity.getTag() == null) {
            return null;
        }
        Map<Long, Long> lookup = getCategoryDictionary(shoppingList.getListLayoutId(), Arrays.asList(itemEntity.getTag()));
        if (lookup.isEmpty()) {
            return null;
        }
        return lookup.get(itemEntity.getTag().getId());
    }
}
