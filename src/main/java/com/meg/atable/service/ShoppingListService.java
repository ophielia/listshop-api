package com.meg.atable.service;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.api.model.Category;
import com.meg.atable.api.model.GenerateType;
import com.meg.atable.api.model.ListGenerateProperties;
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

    ShoppingListEntity createList(String userName, ListGenerateProperties listGeneratProperties) throws ShoppingListException, ObjectNotYoursException, ObjectNotFoundException;

    ShoppingListEntity getListByUsernameAndType(String userName, ListType listType);

    ShoppingListEntity getListById(String userName, Long listId);

    boolean deleteList(String userName, Long listId);

    void addItemToList(String name, Long listId, ItemEntity itemEntity);

    void deleteItemFromList(String name, Long listId, Long itemId, Boolean removeEntireItem, Long dishSourceId);

    ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    ShoppingListEntity setListActive(String username, Long listId, GenerateType generateType);

    List<Category> categorizeList(String userName, ShoppingListEntity shoppingListEntity, Long highlightDishId, Boolean showPantry, ListType highlightDishType);

    void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException;

    void fillSources(ShoppingListEntity result);

    void changeListLayout(String name, Long listId, Long layoutId);

    void removeDishFromList(String name, Long listId, Long dishId);

    void removeListItemsFromList(String name, Long listId, ListType listType);

    void updateItemCrossedOff(String name, Long listId, Long itemId, Boolean crossedOff);

    void crossOffAllItems(String name, Long listId, boolean crossOff);

    void deleteAllItemsFromList(String name, Long listId);

    void addListToList(String name, Long listId, ListType listType);



}
