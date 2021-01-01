package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.*;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/shoppinglist")
@CrossOrigin
public interface ShoppingListRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createList(Principal principal, @RequestBody ListGenerateProperties listGenerateProperties);

    @PutMapping(value = "/shared", produces = "application/json")
    ResponseEntity<MergeResultResource> mergeList(Principal principal, @RequestBody MergeRequest mergeRequest);

    @GetMapping(value = "/shared/{listLayoutId}", produces = "application/json")
    ResponseEntity<List<ListItemRefreshResource>> refreshListItems(Principal principal, @PathVariable("listLayoutId") Long listLayoutId, @RequestParam(value = "after", required = true) Date changedAfter);

    @PutMapping(value = "/{listId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateList(Principal principal, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList);

    @PutMapping(value = "/{listId}/item", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateItems(Principal principal, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation);

    @GetMapping(value = "/mostrecent", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveMostRecentList(Principal principal);

    @GetMapping(value = "/starter", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveStarterList(Principal principal);

    @GetMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId);


    @DeleteMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId);

    @PostMapping(value = "/{listId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addItemToListByTag(Principal principal, @PathVariable Long listId, @PathVariable Long tagId);

    @DeleteMapping(value = "/{listId}/item/{itemId}", produces = "application/json")
    ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                              @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                              @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId
    );

    @PostMapping(value = "/{listId}/item/shop/{itemId}", produces = "application/json")
    ResponseEntity<Object> setCrossedOffForItem(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                              @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    );

    @PostMapping(value = "/{listId}/item/shop", produces = "application/json")
    ResponseEntity<Object> crossOffAllItemsOnList(Principal principal, @PathVariable Long listId,
                                                  @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff);

    @DeleteMapping(value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> deleteAllItemsFromList(Principal principal, @PathVariable Long listId);

    @PostMapping(value = "/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> generateListFromMealPlan(Principal principal, @PathVariable Long mealPlanId);

    @PutMapping(value = "/{listId}/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> addToListFromMealPlan(Principal principal, @PathVariable Long listId, @PathVariable Long mealPlanId);

    @PostMapping(value = "/{listId}/dish", produces = "application/json")
    public ResponseEntity<Object> addDishesToList(Principal principal, @PathVariable Long listId, @RequestBody ListAddProperties listAddProperties);

    @PostMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId);

    @DeleteMapping(value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> removeDishFromList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId);

    @PostMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> addToListFromList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId);


    @DeleteMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> removeFromListByList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId);

    @PostMapping(value = "/{listId}/layout/{layoutId}", produces = "application/json")
    ResponseEntity<Object> changeListLayout(Principal principal, @PathVariable Long listId, @PathVariable Long layoutId);

    @PutMapping(value = "/{listId}/tag/{tagId}/count/{usedCount}", produces = "application/json")
    ResponseEntity<Object> updateItemCountByTag(Principal principal, @PathVariable Long listId,
                                                @PathVariable Long tagId,
                                                @PathVariable @NotNull Integer usedCount
    );


}
