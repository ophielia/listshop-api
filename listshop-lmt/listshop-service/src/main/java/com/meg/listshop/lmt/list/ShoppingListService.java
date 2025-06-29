package com.meg.listshop.lmt.list;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ShoppingListService {

    String FREQUENT = "frequent";

    List<ShoppingListEntity> getListsByUserId(Long userId);

    ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGeneratProperties) throws ShoppingListException;

    ShoppingListEntity getStarterList(Long userId);

    ShoppingListEntity getMostRecentList(Long userId);

    ShoppingListEntity getListForUserById(Long userId, Long listId);

    ShoppingListEntity getSimpleListForUserById(Long userId, Long listId);

    void deleteList(Long userId, Long listId);

    ShoppingListEntity updateList(Long userId, Long listId, ShoppingListEntity updateFrom);

    void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId);

    void deleteItemFromList(Long userId, Long listId, Long itemId, Boolean removeEntireItem, Long dishSourceId);

    ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId);

    List<ShoppingListCategory> categorizeList(ShoppingListEntity shoppingListEntity);

    void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException, ItemProcessingException;

    void fillSources(ShoppingListEntity result);

    void changeListLayout(Long userId, Long listId, Long layoutId);

    void removeDishFromList(Long userId, Long listId, Long dishId);

    void removeListItemsFromList(Long userId, Long listId, Long fromListId);

    void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff);

    void crossOffAllItems(Long userId, Long listId, boolean crossOff);

    void deleteAllItemsFromList(Long userId, Long listId);

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
    MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest);

    void addListToList(Long userId, Long listId, Long fromListId);

    void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException;

    void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId);

    void addItemToListByTag(Long userId, Long listId, Long tagId) throws ItemProcessingException;

    void updateItemCount(Long userId, Long listId, Long tagId, Integer usedCount);
}
