package com.meg.atable.service;

import com.meg.atable.api.model.Category;
import com.meg.atable.api.model.GenerateType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ShoppingListService {

    List<ShoppingListEntity> getListsByUsername(String userName);

    ShoppingListEntity createList(String userName, ShoppingListEntity shoppingListEntity);

    ShoppingListEntity getListByUsernameAndType(String userName, ListType listType);

    ShoppingListEntity getListById(String userName, Long listId);

    boolean deleteList(String userName, Long listId);

    void addItemToList(String name, Long listId, ItemEntity itemEntity);

    void deleteItemFromList(String name, Long listId, Long itemId);

    ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId);

    ShoppingListEntity setListActive(String username, Long listId, GenerateType generateType);

    List<Category> categorizeList(ShoppingListEntity shoppingListEntity);

    void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException;
}
