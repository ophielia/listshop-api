package com.meg.atable.service.impl;

import com.meg.atable.api.model.*;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.*;
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
    @Autowired
    private
    UserService userService;
    @Autowired
    private ListTagStatisticService listTagStatisticService;
    @Autowired
    private TagService tagService;
    @Autowired
    private DishService dishService;
    @Autowired
    private
    ShoppingListRepository shoppingListRepository;
    @Autowired
    private
    ListLayoutService listLayoutService;
    @Autowired
    private
    ListSearchService listSearchService;
    @Autowired
    private
    MealPlanService mealPlanService;
    @Autowired
    private
    ItemRepository itemRepository;


    @Autowired
    private ShoppingListProperties shoppingListProperties;

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

        // fill in tag, if item contains tag
        if (itemEntity.getTagId() != null) {
            // get existing item, if any
            ItemEntity existingItem = getListItemByTag(listId, itemEntity.getTagId());
            if (existingItem != null) {
                int count = existingItem.getUsedCount() != null ? existingItem.getUsedCount() : 0;
                existingItem.setUsedCount(count + 1);
                itemEntity = existingItem;
            } else {
                TagEntity tag = tagService.getTagById(itemEntity.getTagId()).get();
                itemEntity.setTag(tag);
            }
            // increment stats
            UserAccountEntity user = userService.getUserByUserName(name);
            listTagStatisticService.itemAddedToList(user.getId(), itemEntity.getTag().getId(), shoppingListEntity.getListType());
        }

        // prepare item
        itemEntity.setAddedOn(new Date());
        itemEntity.addItemSource(ItemSourceType.Manual);
        itemEntity.setListId(listId);
        ItemEntity result = itemRepository.save(itemEntity);
        // add to shoppingListEntity
        List<ItemEntity> items = shoppingListEntity.getItems();
        items.add(result);
        // save shoppingListEntity (also saving items)
        shoppingListRepository.save(shoppingListEntity);
    }

    private ItemEntity getListItemByTag(Long listId, Long tagId) {
        List<ItemEntity> matches = itemRepository.getItemsForTag(listId, tagId);
        if (matches == null || matches.isEmpty()) {
            return null;
        }
        return matches.get(0);
    }


    @Override
    public void deleteItemFromList(String name, Long listId, Long itemId) {
        ShoppingListEntity shoppingListEntity = getListById(name, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // increment stats
        ItemEntity itemforstats = itemRepository.findOne(itemId);
        if (itemforstats.getTag() != null) {
            UserAccountEntity user = userService.getUserByUserName(name);

            listTagStatisticService.itemRemovedFromList(user.getId(), itemforstats.getTag().getId(), shoppingListEntity.getListType());
        }

        // get items for shopping list
        List<ItemEntity> listItems = itemRepository.findByListId(listId);

        // filter items removing item to be deleted
        List<ItemEntity> filteredItems = listItems.stream()
                .filter(i -> !i.getId().equals(itemId.longValue()))
                .collect(Collectors.toList());

        // delete item
        itemRepository.delete(itemId);

        // set filtered items in shopping list
        shoppingListEntity.setItems(filteredItems);

        // save shopping list
        shoppingListRepository.save(shoppingListEntity);
    }

    @Override
    public ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        // get list layout by type
        ListLayoutType inprocessLayout = shoppingListProperties.getDefaultLayouts().get(ListType.InProcess);
        ListLayoutEntity listLayoutEntity = listLayoutService.getListLayouts()
                .stream()
                .filter(t -> t.getLayoutType().equals(inprocessLayout))
                .findFirst()
                .get();

        // get existing inprocess list, and delete it
        ShoppingListEntity inProcess = getListByUsernameAndType(name, ListType.InProcess);
        if (inProcess != null) {
            shoppingListRepository.delete(inProcess);
        }
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(name, mealPlanId);
        if (mealPlan == null) {
            return null;
        }

        // create new inprocess list
        ShoppingListEntity newList = new ShoppingListEntity();
        newList.setListLayoutId(listLayoutEntity.getId());
        newList.setListType(ListType.InProcess);
        ShoppingListEntity savedNewList = createList(name, newList);
        // create the collector
        ListItemCollector collector = new ListItemCollector(savedNewList.getId());


        // add all tags
        for (SlotEntity slot : mealPlan.getSlots()) {
            List<TagType> tagTypeList = new ArrayList<>();
            tagTypeList.add(TagType.Ingredient);
            tagTypeList.add(TagType.NonEdible);
            List<TagEntity> tags = tagService.getTagsForDish(slot.getDish().getId(), tagTypeList);
            collector.addTags(tags, slot.getDish().getId());
        }

        // add Items from BaseList
        ShoppingListEntity baseList = getListByUsernameAndType(name, ListType.BaseList);
        if (baseList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.BaseList, baseList.getItems());
        }

        // process statistics (don't want to include pickup in statistics)
        listTagStatisticService.processStatistics(user.getId(), collector);

        // add Items from PickUpList
        ShoppingListEntity pickupList = getListByUsernameAndType(name, ListType.PickUpList);
        if (pickupList != null) {
            collector.copyExistingItemsIntoList(ItemSourceType.PickUpList, pickupList.getItems());
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        // get the collected items, and save them
        List<ItemEntity> savedItems = itemRepository.save(collector.getItems());
        // add items to the list, and save the list
        savedNewList.setItems(savedItems);
        return shoppingListRepository.save(savedNewList);

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
            collector.copyExistingItemsIntoList(ItemSourceType.PickUpList, oldActive.getItems());
        }

        // cross items off of pickup list
        ShoppingListEntity pickuplist = getListByUsernameAndType(user.getUsername(), ListType.PickUpList);
        deleteAllItemsFromList(pickuplist.getId());

        // delete old active list
        if (oldActive != null) {
            deleteList(username, oldActive.getId());
        }
        // set current list active
        toActive.setListType(ListType.ActiveList);
        // save active list
        ShoppingListEntity result = shoppingListRepository.save(toActive);
        return result;

    }

    public List<Category> categorizeList(ShoppingListEntity shoppingListEntity, Long highlightDishId) {
        boolean isHighlightDish = highlightDishId!=null;
        boolean separateFrequent = shoppingListEntity.getListType().equals(ListType.InProcess);

        Set<Long> dishItemIds = getDishItemIds(shoppingListEntity,highlightDishId);
        String highlightName ="";
        if (isHighlightDish) {
            DishEntity dish = dishService.getDishById(highlightDishId).get();
            highlightName = dish.getDishName();
        }

        if (shoppingListEntity.getItems() == null || shoppingListEntity.getItems().isEmpty()) {
            return new ArrayList<Category>();
        }

        // get category to item dictionary (tag key, category value)
        List<TagEntity> tagList = shoppingListEntity.getItems().stream().map(ItemEntity::getTag).collect(Collectors.toList());
        Map<Long, Long> dictionary = getCategoryDictionary(shoppingListEntity.getListLayoutId(), tagList);
        // get categories for items
        Set<Long> categoryIds = new HashSet<>(dictionary.values());
        List<ListLayoutCategoryEntity> categoriesEntities = listLayoutService.getListCategoriesForIds(categoryIds);
        if (categoriesEntities == null) {
            return null;
        }
        // fill categories for items (into hash)
        Map<Long, Category> filledCategories = new HashMap<>();
        categoriesEntities.forEach(ce -> {
            ItemCategory cat = (ItemCategory) new ItemCategory(ce.getName(), ce.getId())
                    .displayOrder(ce.getDisplayOrder());
            filledCategories.put(cat.getId(), cat);
        });
        ItemCategory frequent = (ItemCategory) new ItemCategory(shoppingListProperties.getFrequentCategoryName(),
                shoppingListProperties.getFrequentIdAndSortAsLong())
                .displayOrder(shoppingListProperties.getFrequentIdAndSort());
        ItemCategory uncategorized = (ItemCategory) new ItemCategory(shoppingListProperties.getUncategorizedCategoryName(),
                shoppingListProperties.getUncategorizedIdAndSortAsLong())
                .displayOrder(shoppingListProperties.getUncategorizedIdAndSort());
        ItemCategory highlight = (ItemCategory) new ItemCategory(highlightName,
                shoppingListProperties.getHighlightIdAndSortAsLong())
                .displayOrder(shoppingListProperties.getHighlightIdAndSort());
        for (ItemEntity item : shoppingListEntity.getItems() ) {
            if (item.isFrequent() && separateFrequent && !isHighlightDish) {
                frequent.addItemEntity(item);
            } else if (!dictionary.containsKey(item.getTag().getId())) {
                uncategorized.addItemEntity(item);
            } else if (isHighlightDish && dishItemIds.contains(item.getId())) {
                highlight.addItemEntity(item);
            } else {

                ItemCategory category = (ItemCategory) filledCategories.get(dictionary.get(item.getTag().getId()));
                if (category == null) {
                    uncategorized.addItemEntity(item);
                } else {
                    category.addItemEntity(item);
                }

            }
        }

        // structure categories
        listLayoutService.structureCategories(filledCategories, shoppingListEntity.getListLayoutId());

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

    private Set<Long> getDishItemIds(ShoppingListEntity shoppingListEntity, Long dishId) {
        Set<Long> dishItemIds = new HashSet<>();
        List<ItemEntity> items = itemRepository.getItemsForDish(shoppingListEntity.getId(),dishId);
        if (items == null || items.isEmpty()) {
            return dishItemIds;
        }
        dishItemIds = items.stream().map(t -> t.getId()).collect(Collectors.toSet());
        return dishItemIds;
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
        collector.addTags(tagsForDish, dishId);

        // update last added date for dish
        dishService.updateLastAddedForDish(dishId);
        // get the collected items, and save them
        List<ItemEntity> savedItems = itemRepository.save(collector.getItems());
        // add items to the list, and save the list
        list.setItems(savedItems);
        shoppingListRepository.save(list);
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
                source = String.join(";",rawSources);
            }
            // put into set
            Set<Long> dishIdSet =  FlatStringUtils.inflateStringToLongSet(source,";");
            List<Long> dishIds =  new ArrayList<>();
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
                source = String.join(";",listRawSources);
            }
            // put into set
            Set<String> listSourceSet =  FlatStringUtils.inflateStringToSet(source,";");
            List<String> listSources =  new ArrayList<>();
            listSources.addAll(listSourceSet);
            // set in shopping list
            result.setListSources(listSources);
        }
        return;
    }

    @Override
    public void changeListLayout(String name, Long listId, Long layoutId) {
        // get shopping list
        ShoppingListEntity shoppingList = getListById(name,listId);
        // set new layout id in shopping list
        shoppingList.setListLayoutId(layoutId);
        // save shopping list
        shoppingListRepository.save(shoppingList);

    }

    @Override
    public void removeDishFromList(String name, Long listId, Long dishId) {
        // get list
        ShoppingListEntity shoppingList = getListById(name, listId);
        // get items for dish
        List<ItemEntity> items = itemRepository.getItemsForDish(listId,dishId);

        // go through each item
        List<ItemEntity> toUpdate = new ArrayList<>();
        List<ItemEntity> toDelete = new ArrayList<>();
        for (ItemEntity item: items) {
            if (item.getUsedCount()>1) {
            // if used count more than one, decrement, and remove dish source
        // add to update list
                item.setUsedCount(item.getUsedCount()-1);
                Set<String> inflatedDishSources = FlatStringUtils.inflateStringToSet(item.getRawDishSources(),";");
                if (inflatedDishSources.contains(String.valueOf(dishId))) {
                    inflatedDishSources.remove(String.valueOf(dishId));
                    String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources,";");
                    item.setRawDishSources(newSources);
                }
                toUpdate.add(item);
            } else {
        // if used count is one, add to delete list
                toDelete.add(item);
            }
        }
        // update items to update
        itemRepository.save(toUpdate);
        // delete items to delete
        itemRepository.delete(toDelete);
        // make changes in list object
        ListItemCollector collector = new ListItemCollector(listId, shoppingList.getItems());
        collector.updateItems(toUpdate);
        collector.removeItems(toDelete);
        shoppingList.setItems(collector.getItems());
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
