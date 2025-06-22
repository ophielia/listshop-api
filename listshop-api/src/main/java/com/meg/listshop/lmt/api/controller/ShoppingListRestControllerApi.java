package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;


/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/shoppinglist")
@CrossOrigin
public interface ShoppingListRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<ShoppingListListResource> retrieveLists(HttpServletRequest request, Authentication authentication);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createList(HttpServletRequest request, Authentication principal, @RequestBody ListGenerateProperties listGenerateProperties);

    @PutMapping(value = "/shared", produces = "application/json")
    ResponseEntity<MergeResultResource> mergeList(Authentication principal, @RequestBody MergeRequest mergeRequest);

    @PutMapping(value = "/{listId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateList(HttpServletRequest request, Authentication principal, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList);

    @PutMapping(value = "/{listId}/item", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateItems(Authentication principal, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation);

    @GetMapping(value = "/mostrecent", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveMostRecentList(HttpServletRequest request, Authentication principal);

    @GetMapping(value = "/starter", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveStarterList(HttpServletRequest request, Authentication principal);

    @GetMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveListById(HttpServletRequest request, Authentication principal, @PathVariable("listId") Long listId);


    @DeleteMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingList> deleteList(Authentication principal, @PathVariable("listId") Long listId);

    @PostMapping(value = "/{listId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addItemToListByTag(Authentication principal, @PathVariable Long listId, @PathVariable Long tagId) throws ItemProcessingException;

    @DeleteMapping(value = "/{listId}/item/{itemId}", produces = "application/json")
    ResponseEntity<Object> deleteItemFromList(Authentication principal, @PathVariable Long listId, @PathVariable Long itemId,
                                              @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                              @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId
    );

    @PostMapping(value = "/{listId}/item/shop/{itemId}", produces = "application/json")
    ResponseEntity<Object> setCrossedOffForItem(Authentication principal, @PathVariable Long listId, @PathVariable Long itemId,
                                                @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    );

    @PostMapping(value = "/{listId}/item/shop", produces = "application/json")
    ResponseEntity<Object> crossOffAllItemsOnList(Authentication principal, @PathVariable Long listId,
                                                  @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff);

    @DeleteMapping(value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> deleteAllItemsFromList(Authentication principal, @PathVariable Long listId);

    @PostMapping(value = "/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> generateListFromMealPlan(HttpServletRequest request, Authentication principal, @PathVariable Long mealPlanId);

    @PutMapping(value = "/{listId}/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> addToListFromMealPlan(Authentication principal, @PathVariable Long listId, @PathVariable Long mealPlanId);

    @PostMapping(value = "/{listId}/dish", produces = "application/json")
    ResponseEntity<Object> addDishesToList(Authentication principal, @PathVariable Long listId, @RequestBody ListAddProperties listAddProperties);

    @PostMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToList(Authentication principal, @PathVariable Long listId, @PathVariable Long dishId);

    @DeleteMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> removeDishFromList(Authentication principal, @PathVariable Long listId, @PathVariable Long dishId);

    @PostMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> addToListFromList(Authentication principal, @PathVariable Long listId, @PathVariable Long fromListId);


    @DeleteMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> removeFromListByList(Authentication principal, @PathVariable Long listId, @PathVariable Long fromListId);

    @PostMapping(value = "/{listId}/layout/{layoutId}", produces = "application/json")
    ResponseEntity<Object> changeListLayout(Authentication principal, @PathVariable Long listId, @PathVariable Long layoutId);

    @PutMapping(value = "/{listId}/tag/{tagId}/count/{usedCount}", produces = "application/json")
    ResponseEntity<Object> updateItemCountByTag(Authentication principal, @PathVariable Long listId,
                                                @PathVariable Long tagId,
                                                @PathVariable @NotNull Integer usedCount
    );


}
