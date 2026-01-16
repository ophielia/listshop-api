package com.meg.listshop.lmt.list;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Transactional
public interface ShoppingListService {

    String FREQUENT = "frequent";

    List<ShoppingListEntity> getListsByUserId(Long userId);

    void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException, ItemProcessingException;

    ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGeneratProperties) throws ShoppingListException, ItemProcessingException;

    ShoppingListEntity getStarterList(Long userId);

    ShoppingListEntity getMostRecentList(Long userId);

    ShoppingListEntity getListForUserById(Long userId, Long listId);

    ShoppingListEntity getSimpleListForUserById(Long userId, Long listId);

    void deleteList(Long userId, Long listId);

    ShoppingListEntity updateList(Long userId, Long listId, ShoppingListEntity updateFrom);

    void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) throws ItemProcessingException;

    void deleteItemFromList(Long userId, Long listId, Long itemId) throws ItemProcessingException;

    ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId) throws ShoppingListException, ItemProcessingException;

    List<ShoppingListCategory> categorizeList(ShoppingListEntity shoppingListEntity);

    void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException, ItemProcessingException;

    void fillSources(ShoppingListEntity result);

    void changeListLayout(Long userId, Long listId, Long layoutId);

    void removeDishFromList(Long userId, Long listId, Long dishId) throws ItemProcessingException;

    void removeListItemsFromList(Long userId, Long listId, Long fromListId) throws ItemProcessingException;

    void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff) throws ItemProcessingException;

    void crossOffAllItems(Long userId, Long listId, boolean crossOff) throws ItemProcessingException;

    void deleteAllItemsFromList(Long userId, Long listId) throws ItemProcessingException;

    // Note - this method doesn't check yet for MergeConflicts.  But the signature
    // is there to build the interface, so that MergeConflicts can be added later
    // less painfully.  Right now just going for basic functionality - taking the
    // last modified item.
    MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest);

    void addListToList(Long userId, Long listId, Long fromListId) throws ItemProcessingException;

    void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId) throws ShoppingListException, ItemProcessingException;

    void addItemToListByTag(Long userId, Long listId, Long tagId) throws ItemProcessingException;

    void updateItemCount(Long userId, Long listId, Long tagId, Integer usedCount);
}
