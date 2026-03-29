/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.ItemOperationPut;
import com.meg.listshop.lmt.api.model.ListAddProperties;
import com.meg.listshop.lmt.api.model.ListGenerateProperties;
import com.meg.listshop.lmt.api.model.v2.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;


/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/v2/shoppinglist")
@CrossOrigin
public interface V2ShoppingListRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<ShoppingListList> retrieveLists(HttpServletRequest request, Authentication authentication);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createList(HttpServletRequest request, Authentication principal, @RequestBody ListGenerateProperties listGenerateProperties) throws MalformedURLException;

    @PutMapping(value = "/shared", produces = "application/json")
    ResponseEntity<MergeResult> mergeList(Authentication authentication, @RequestBody MergeRequest mergeRequest);

    @PutMapping(value = "/{listId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateList(HttpServletRequest request, Authentication principal, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList);

    @PutMapping(value = "/{listId}/item", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateItems(Authentication principal, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation);

    @GetMapping(value = "/mostrecent", produces = "application/json")
    ResponseEntity<ShoppingList> retrieveMostRecentList(HttpServletRequest request, Authentication principal);

    @GetMapping(value = "/starter", produces = "application/json")
    ResponseEntity<ShoppingList> retrieveStarterList(HttpServletRequest request, Authentication principal);

    @GetMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingList> retrieveListById(HttpServletRequest request, Authentication principal, @PathVariable("listId") Long listId);


    @DeleteMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<Object> deleteList(Authentication principal, @PathVariable("listId") Long listId);

    @PostMapping(value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> addItemToList(Authentication principal, @PathVariable("listId") Long listId,
                                         @RequestBody PostListItem postListItem) throws ItemProcessingException;

    @DeleteMapping(value = "/{listId}/item/{itemId}", produces = "application/json")
    ResponseEntity<Object> deleteItemFromList(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("itemId") Long itemId,
                                              @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                              @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId
    );

    @PostMapping(value = "/{listId}/item/shop/{itemId}", produces = "application/json")
    ResponseEntity<Object> setCrossedOffForItem(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("itemId") Long itemId,
                                                @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    ) throws ItemProcessingException;

    @PostMapping(value = "/{listId}/item/shop", produces = "application/json")
    ResponseEntity<Object> crossOffAllItemsOnList(Authentication principal, @PathVariable("listId") Long listId,
                                                  @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff) throws ItemProcessingException;

    @DeleteMapping(value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> deleteAllItemsFromList(Authentication principal, @PathVariable("listId") Long listId);

    @PostMapping(value = "/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> generateListFromMealPlan(HttpServletRequest request, Authentication principal, @PathVariable("mealPlanId") Long mealPlanId) throws MalformedURLException;

    @PutMapping(value = "/{listId}/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> addToListFromMealPlan(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("mealPlanId") Long mealPlanId);

    @PostMapping(value = "/{listId}/dish", produces = "application/json")
    ResponseEntity<Object> addDishesToList(Authentication principal, @PathVariable("listId") Long listId, @RequestBody ListAddProperties listAddProperties);

    @PostMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToList(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("dishId") Long dishId);

    @DeleteMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> removeDishFromList(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("dishId") Long dishId) ;

    @PostMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> addToListFromList(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("fromListId") Long fromListId);


    @DeleteMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> removeFromListByList(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("fromListId") Long fromListId);

    @PostMapping(value = "/{listId}/layout/{layoutId}", produces = "application/json")
    ResponseEntity<Object> changeListLayout(Authentication principal, @PathVariable("listId") Long listId, @PathVariable("layoutId") Long layoutId);

    @PutMapping(value = "/{listId}/tag/{tagId}/count/{usedCount}", produces = "application/json")
    ResponseEntity<Object> updateItemCountByTag(Authentication principal, @PathVariable("listId") Long listId,
                                                @PathVariable("tagId") Long tagId,
                                                @PathVariable("usedCount") @NotNull Integer usedCount
    );


}
