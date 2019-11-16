package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.Category;
import com.meg.atable.lmt.api.model.ListGenerateProperties;
import com.meg.atable.lmt.api.model.MergeRequest;
import com.meg.atable.lmt.api.model.MergeResult;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ShoppingListService {

    List<ShoppingListEntity> getListsByUsername(String userName);

    ShoppingListEntity generateListForUser(String userName, ListGenerateProperties listGeneratProperties) throws ShoppingListException;

    ShoppingListEntity getStarterList(String userName);

    ShoppingListEntity getMostRecentList(String userName);

    ShoppingListEntity getListById(String userName, Long listId);

    ShoppingListEntity getListById(String userName, Long listId, boolean includeRemoved);

    boolean deleteList(String userName, Long listId);

    ShoppingListEntity updateList(String name, Long listId, ShoppingListEntity updateFrom);

    void addItemToList(String name, Long listId, ItemEntity itemEntity);

    void addItemToListByTag(String name, Long listId, Long tagId);

    void deleteItemFromList(String name, Long listId, Long itemId, Boolean removeEntireItem, Long dishSourceId);

    ShoppingListEntity generateListFromMealPlan(String name, Long mealPlanId);

    List<Category> categorizeList(String userName, ShoppingListEntity shoppingListEntity, Long highlightDishId, Boolean showPantry, Long highlightListId);

    void addDishToList(String name, Long listId, Long dishId) throws ShoppingListException;

    void fillSources(ShoppingListEntity result);

    void changeListLayout(String name, Long listId, Long layoutId);

    void removeDishFromList(String name, Long listId, Long dishId);

    void removeListItemsFromList(String name, Long listId, Long fromListId);

    void updateItemCrossedOff(String name, Long listId, Long itemId, Boolean crossedOff);

    void crossOffAllItems(String name, Long listId, boolean crossOff);

    void deleteAllItemsFromList(String name, Long listId);

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
    MergeResult mergeFromClient(String userName, MergeRequest mergeRequest);

    void addListToList(String name, Long listId, Long fromListId);

    List<ItemEntity> getChangedItemsForMostRecentList(String name, Date changedAfter, Long layoutId);


    ShoppingListEntity addToListFromMealPlan(String name, Long listId, Long mealPlanId);
}
