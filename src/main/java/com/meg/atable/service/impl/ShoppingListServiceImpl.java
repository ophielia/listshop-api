package com.meg.atable.service.impl;

import com.meg.atable.api.model.ItemSourceType;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ItemRepository;
import com.meg.atable.data.repository.ShoppingListRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.ListItemCollector;
import com.meg.atable.service.ListTagStatisticService;
import com.meg.atable.service.MealPlanService;
import com.meg.atable.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    @Autowired
    private
    UserService userService;

    @Autowired
    private ListTagStatisticService listTagStatisticService;

    @Autowired
    private
    ShoppingListRepository shoppingListRepository;

    @Autowired
    private
    MealPlanService mealPlanService;

    @Autowired
    private
    ItemRepository itemRepository;

    @Autowired
    private
    TagRepository tagRepository;

    // MM need to handle this in application settings /user settings
    private final static ListLayoutType listlayoutdefault = ListLayoutType.All;

    @Override
    public List<ShoppingListEntity> getListsByUsername(String userName) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        return shoppingListRepository.findByUserId(user.getId());
    }

    @Override
    public ShoppingListEntity createList(String userName, ShoppingListEntity shoppingList) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        shoppingList.setCreatedOn(new Date());
        shoppingList.setUserId(user.getId());
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingListEntity getListByUsernameAndType(String userName, ListType listType) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        return shoppingListRepository.findByUserIdAndListType(user.getId(), listType);
    }

    @Override
    public ShoppingListEntity getListById(String userName, Long listId) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findOne(listId);
        if (shoppingListEntity != null && shoppingListEntity.getUserId() == user.getId()) {
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
            TagEntity tag = tagRepository.findOne(itemEntity.getTagId());
            itemEntity.setTag(tag);

            // increment stats
            UserAccountEntity user = userService.getUserByUserName(name);
            listTagStatisticService.itemAddedToList(user.getId(), tag.getId(), shoppingListEntity.getListType());
        }
        // prepare item
        itemEntity.setAddedOn(new Date());
        itemEntity.addItemSource(ItemSourceType.Manual);
        itemEntity.setListId(listId);
        String listCategory = getCategoryForItem(itemEntity, shoppingListEntity.getListLayoutType());
        itemEntity.setListCategory(listCategory);
        ItemEntity result = itemRepository.save(itemEntity);
        // add to shoppingListEntity
        List<ItemEntity> items = shoppingListEntity.getItems();
        items.add(result);
        // save shoppingListEntity (also saving items)
        shoppingListRepository.save(shoppingListEntity);
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
                .filter(i -> i.getId().longValue() != itemId.longValue())
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

        List<TagEntity> allMealPlanTags = mealPlanService.fillInDishTags(mealPlan);
        // get the tagcategorykey for the mealplan
        Map<Long, String> categoryDictionary = getCategoryDictionary(allMealPlanTags);
        // create new inprocess list
        ShoppingListEntity newList = new ShoppingListEntity();
        newList.setListLayoutType(listlayoutdefault);
        newList.setListType(ListType.InProcess);
        ShoppingListEntity savedNewList = createList(name, newList);
        // create the collector
        ListItemCollector collector = new ListItemCollector(savedNewList.getId(), categoryDictionary);

        // add all tags
        collector.addTags(allMealPlanTags);

        // add Items from BaseList
        ShoppingListEntity baseList = getListByUsernameAndType(name, ListType.BaseList);
        if (baseList != null) {
            collector.addListItems(ListType.BaseList, ItemSourceType.BaseList, baseList.getItems());
        }

        // process statistics (don't want to include pickup in statistics)
        listTagStatisticService.processStatistics(user.getId(), collector);

        // add Items from PickUpList
        ShoppingListEntity pickupList = getListByUsernameAndType(name, ListType.PickUpList);
        if (pickupList != null) {
            collector.addListItems(ListType.PickUpList, ItemSourceType.PickUpList, pickupList.getItems());
        }

        // check categorization of other list items
        List<TagEntity> uncategorized = collector.getUncategorizedTags();
        if (uncategorized != null && !uncategorized.isEmpty()) {
            Map<Long, String> dictionary = getCategoryDictionary(uncategorized);
            collector.categorizeUncategorized(dictionary);
        }

        // update the last added date for dishes
        mealPlanService.updateLastAddedDateForDishes(mealPlan);
        // get the collected items, and save them
        List<ItemEntity> savedItems = itemRepository.save(collector.getItems());
        // add items to the list, and save the list
        savedNewList.setItems(savedItems);
        return shoppingListRepository.save(savedNewList);

    }

    private Map<Long, String> getCategoryDictionary(List<TagEntity> tagList) {
        // MM dummy method until list layouts are implemented

        Map<Long, String> dictionary = new HashMap<>();
        tagList.stream()
                .forEach(t -> dictionary.put(t.getId(), "ALL"));
        return dictionary;
    }

    private String getCategoryForItem(ItemEntity itemEntity, ListLayoutType listLayoutType) {
        // MM needs real implementation with layouts
        return "ALL";
    }
}
