/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.v2;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;

import com.meg.listshop.lmt.api.model.ItemOperationType;
import com.meg.listshop.lmt.api.model.ListAddProperties;
import com.meg.listshop.lmt.api.model.ListGenerateProperties;
import com.meg.listshop.lmt.api.model.v2.MergeRequest;
import com.meg.listshop.lmt.api.model.v2.MergeResult;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.pojos.CategoryDTO;
import com.meg.listshop.lmt.data.pojos.ShoppingListDTO;
import com.meg.listshop.lmt.data.pojos.SourceDTO;
import com.meg.listshop.lmt.list.ShoppingListException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Transactional
public interface V2ShoppingListService {

    String FREQUENT = "frequent";

    List<ShoppingListDTO> getListsByUserId(Long userId);

    void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException, ItemProcessingException;

    ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGeneratProperties) throws ShoppingListException, ItemProcessingException;

    ShoppingListDTO getStarterList(Long userId);

    ShoppingListDTO getMostRecentList(Long userId);

    ShoppingListEntity getListForUserById(Long userId, Long listId);

    ShoppingListEntity getSimpleListForUserById(Long userId, Long listId);

    void deleteList(Long userId, Long listId);

    ShoppingListEntity updateList(Long userId, Long listId, ShoppingListDTO updateFrom);

    void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) throws ItemProcessingException;

    void deleteItemFromList(Long userId, Long listId, Long itemId) throws ItemProcessingException;

    ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId) throws ShoppingListException, ItemProcessingException;

    void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException, ItemProcessingException;

    void fillSources(ShoppingListEntity result);

    void changeListLayout(Long userId, Long listId, Long layoutId);

    void removeDishFromList(Long userId, Long listId, Long dishId) throws ItemProcessingException;

    void removeListItemsFromList(Long userId, Long listId, Long fromListId) throws ItemProcessingException;

    void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff) throws ItemProcessingException;

    void crossOffAllItems(Long userId, Long listId, boolean crossOff) throws ItemProcessingException;

    void deleteAllItemsFromList(Long userId, Long listId) throws ItemProcessingException;

    MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest);

    void addListToList(Long userId, Long listId, Long fromListId) throws ItemProcessingException;

    void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId) throws ShoppingListException, ItemProcessingException;

    void addItemToListByTag(Long userId, Long listId, Long tagId) throws ItemProcessingException;

    void updateItemCount(Long userId, Long listId, Long tagId, Integer usedCount);

    List<CategoryDTO> retrieveListCategories(Long id);

    List<SourceDTO> retrieveListSources(Long id);

    ShoppingListDTO getListDTOForUser(Long userId, Long listId);

    Map<Long, String> retrieveUnitMapping(Long listId);
}
