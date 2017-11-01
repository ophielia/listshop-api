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
import com.meg.atable.service.MealPlanService;
import com.meg.atable.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    private final ListLayoutType listlayoutdefault = ListLayoutType.All;

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
        }
        // prepare item
        itemEntity.setAddedOn(new Date());
        itemEntity.setItemSource(ItemSourceType.Manual);
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
        // get existing inprocess list, and delete it
        ShoppingListEntity inProcess = getListByUsernameAndType(name,ListType.InProcess);
        if (inProcess != null) {
            shoppingListRepository.delete(inProcess);
        }
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(name,mealPlanId);
        if (mealPlan==null) {
            return null;
        }
        mealPlanService.fillInDishTags(mealPlan);
        // create new inprocess list
        ShoppingListEntity newList = new ShoppingListEntity();
        newList.setListLayoutType(listlayoutdefault);
        newList.setListType(ListType.InProcess);
        ShoppingListEntity savedNewList = createList(name,newList);

        // get the tagcategorykey for the mealplan
        Map<Long,String> categoryDictionary = getCategoryDictionary(mealPlan);
        // create list of items, categorizing as you go
        Map<TagEntity,Long> tagCount = mealPlan.getAllTags()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        List<ItemEntity> items = tagCount.entrySet()
                .stream()
                .map(e -> {
                    ItemEntity item = new ItemEntity();
                    item.setTag(e.getKey());
                    item.setListId(savedNewList.getId());
                    item.setItemSource(ItemSourceType.MealPlan);
                    item.setUsedCount(e.getValue().intValue());
                    item.setListCategory(categoryDictionary.get(e.getKey().getId()));
                    return item;
        })
                .collect(Collectors.toList());

        // add items to in process list
        List<ItemEntity> savedItems = itemRepository.save(items);
        savedNewList.setItems(savedItems);
        // save in process list
        return shoppingListRepository.save(savedNewList);
    }

    private Map<Long, String> getCategoryDictionary(MealPlanEntity mealPlan) {
        // MM dummy method until list layouts are implemented

        Map<Long, String> dictionary = new HashMap<>();
        mealPlan.getAllTags()
                .stream()
                .forEach(t -> dictionary.put(t.getId(),"ALL"));
           return dictionary;
    }

    private String getCategoryForItem(ItemEntity itemEntity, ListLayoutType listLayoutType) {
        // MM needs real implementation with layouts
        return "ALL";
    }
}
