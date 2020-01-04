package com.meg.atable.lmt.api.web.controller;

import com.google.common.base.Enums;
import com.meg.atable.lmt.api.controller.ShoppingListRestControllerApi;
import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.lmt.service.ShoppingListException;
import com.meg.atable.lmt.service.ShoppingListService;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ShoppingListRestController implements ShoppingListRestControllerApi {

    private static final Logger logger = LogManager.getLogger(ShoppingListRestController.class);


    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private ListLayoutService listLayoutService;

    public ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal) {

        List<ShoppingListResource> shoppingListResources = shoppingListService
                .getListsByUsername(principal.getName())
                .stream()
                .map(t -> new ShoppingListResource(t, null))
                .collect(Collectors.toList());
        Resources<ShoppingListResource> listResourceList = new Resources<>(shoppingListResources);
        return new ResponseEntity(listResourceList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> createList(Principal principal, @RequestBody ListGenerateProperties listGenerateProperties) {


        ShoppingListEntity result = null;
        try {
            result = shoppingListService.generateListForUser(principal.getName(), listGenerateProperties);
        } catch (ShoppingListException e) {
            logger.error("Exception while creating List.",e);
        }
        if (result != null) {
            Link oneList = new ShoppingListResource(result, null).getLink("self");
            return ResponseEntity.created(URI.create(oneList.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<MergeResultResource> mergeList(Principal principal, @RequestBody MergeRequest mergeRequest) {

        Long listId = mergeRequest.getListId();
        Long layoutId = mergeRequest.getLayoutId();

        MergeResult mergeResult = this.shoppingListService.mergeFromClient(principal.getName(), mergeRequest);

        // check for conflicts (won't be any until we implement this)
        if (mergeResult.getMergeConflicts() == null) {
            // retrieve the list, and put it into the result
            ShoppingListEntity shoppingList = this.shoppingListService.getListById(principal.getName(), listId);
            // possibly set layout id in shopping list
            if (layoutId != null && layoutId != shoppingList.getListLayoutId()) {
                shoppingList.setListLayoutId(layoutId);
            }
            List<Category> categories = shoppingListService.categorizeList(principal.getName(), shoppingList, null, false, null);
            shoppingListService.fillSources(shoppingList);
            MergeResultResource resource = new MergeResultResource(mergeResult, shoppingList, categories);

            return new ResponseEntity(resource, HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<List<ListItemRefreshResource>> refreshListItems(Principal principal, @PathVariable("listLayoutId") Long listLayoutId,
                                                                          @RequestParam(value = "after", required = true)
                                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date changedAfter) {
        List<ItemEntity> changedItems = shoppingListService.getChangedItemsForMostRecentList(principal.getName(), changedAfter, listLayoutId);

        List<Pair<ItemEntity, ListLayoutCategoryEntity>> itemsToCategories = listLayoutService.getItemChangesWithCategories(listLayoutId, changedItems);

        List<ListItemRefreshResource> resourceList = new ArrayList<>();
        for (Pair<ItemEntity, ListLayoutCategoryEntity> change : itemsToCategories) {
            ListItemRefreshResource refresh = new ListItemRefreshResource(change.getKey(), change.getValue());
            resourceList.add(refresh);
        }

        return new ResponseEntity(resourceList, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Object> updateList(Principal principal, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList) {
        ShoppingListEntity updateFrom = ModelMapper.toEntity(shoppingList);

        ShoppingListEntity result = shoppingListService.updateList(principal.getName(), listId, updateFrom);
        if (result != null) {
            Link oneList = new ShoppingListResource(result, null).getLink("self");
            return ResponseEntity.ok(URI.create(oneList.getHref()));
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> updateItems(Principal principal, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation) {
        logger.debug("beginning updateItems for input: " + itemOperation);
        if (itemOperation == null) {
            return ResponseEntity.badRequest().build();
        }
        // get operation type as enum
        String operationString = itemOperation.getOperation();
        ItemOperationType operationType = Enums.getIfPresent(ItemOperationType.class, operationString).orNull();
        if (operationType == null) {
            return ResponseEntity.badRequest().build();
        }

        // make service call
        Long destinationListId = itemOperation.getDestinationListId();
        List<Long> tagIdsForUpdate = itemOperation.getTagIds();
        shoppingListService.performItemOperation(principal.getName(), listId, operationType, tagIdsForUpdate, destinationListId);
        // return
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ShoppingListResource> retrieveMostRecentList(Principal principal) {
        ShoppingListEntity result = shoppingListService.getMostRecentList(principal.getName());
        if (result == null) {
            throw new ObjectNotFoundException("No lists found for user [" + principal.getName() + "] in retrieveMostRecentList()");
        }
        List<Category> categories = shoppingListService.categorizeList(principal.getName(), result, null, false, null);
        shoppingListService.fillSources(result);
        return singleResult(result, categories);
    }

    public ResponseEntity<ShoppingListResource> retrieveStarterList(Principal principal) {
        ShoppingListEntity result = shoppingListService.getStarterList(principal.getName());
        if (result == null) {
            throw new ObjectNotFoundException("No lists found for user [" + principal.getName() + "] in retrieveStarterList()");
        }
        List<Category> categories = shoppingListService.categorizeList(principal.getName(), result, null, false, null);
        shoppingListService.fillSources(result);
        return singleResult(result, categories);
    }

    @Override
    public ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId,
                                                                 @RequestParam(value = "highlightDish", required = false, defaultValue = "0") Long highlightDish,
                                                                 @RequestParam(value = "highlightListId", required = false, defaultValue = "0") Long highlightListId,
                                                                 @RequestParam(value = "showPantry", required = false, defaultValue = "false") Boolean showPantry) {
        ShoppingListEntity result = shoppingListService.getListById(principal.getName(), listId);
        if (highlightListId == 0) {
            highlightListId = null;
        }
        if (highlightDish == 0) {
            highlightDish = null;
        }
        //MM debug only
        List<ItemEntity> items = result.getItems();
        List<Long> tempids = items.stream().map(ItemEntity::getTag).map(TagEntity::getId).collect(Collectors.toList());
        logger.debug("tagIds are: " + tempids);
        List<Category> categories = shoppingListService.categorizeList(principal.getName(), result, highlightDish, showPantry, highlightListId);
        shoppingListService.fillSources(result);
        return singleResult(result, categories);
    }


    @Override
    public ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId) {
        boolean success = shoppingListService.deleteList(principal.getName(), listId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> addItemToList(Principal principal, @PathVariable Long listId, @RequestBody Item input) {
        ItemEntity itemEntity = ModelMapper.toEntity(input);

        this.shoppingListService.addItemToList(principal.getName(), listId, itemEntity);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> addItemToListByTag(Principal principal, @PathVariable Long listId, @PathVariable Long tagId) {
        this.shoppingListService.addItemToListByTag(principal.getName(), listId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                                     @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                                     @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId) {
        Long serviceSourceId = null;
        if (!"0".equals(sourceId)) {
            serviceSourceId = Long.valueOf(sourceId);
        }

        this.shoppingListService.deleteItemFromList(principal.getName(), listId, itemId, removeEntireItem, serviceSourceId);

        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{listId}/item/{itemId}", produces = "application/json")
    public ResponseEntity<Object> setCrossedOffForItem(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                                @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    ) {
        this.shoppingListService.updateItemCrossedOff(principal.getName(), listId, itemId, crossedOff);

        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{listId}/item/shop", produces = "application/json")
    public ResponseEntity<Object> crossOffAllItemsOnList(Principal principal, @PathVariable Long listId,
                                                         @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff) {
        this.shoppingListService.crossOffAllItems(principal.getName(), listId, crossedOff);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> deleteAllItemsFromList(Principal principal, @PathVariable Long listId) {
        this.shoppingListService.deleteAllItemsFromList(principal.getName(), listId);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> generateListFromMealPlan(Principal principal, @PathVariable Long mealPlanId) {
        ShoppingListEntity shoppingListEntity = this.shoppingListService.generateListFromMealPlan(principal.getName(), mealPlanId);
        if (shoppingListEntity != null) {
            Link listLink = new ShoppingListResource(shoppingListEntity, null).getLink("self");
            return ResponseEntity.created(URI.create(listLink.getHref())).build();

        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addToListFromMealPlan(Principal principal, @PathVariable Long listId, @PathVariable Long mealPlanId) {
        this.shoppingListService.addToListFromMealPlan(principal.getName(), listId, mealPlanId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addDishToList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId) {
        try {
            this.shoppingListService.addDishToList(principal.getName(), listId, dishId);
        } catch (ShoppingListException s) {
            logger.error("Unable to add Dish [" + dishId + "] to List [" + listId + "]", s);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> removeDishFromList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId) {
        this.shoppingListService.removeDishFromList(principal.getName(), listId, dishId);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addToListFromList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId) {

        this.shoppingListService.addListToList(principal.getName(), listId, fromListId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> removeFromListByList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId) {

        this.shoppingListService.removeListItemsFromList(principal.getName(), listId, fromListId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> changeListLayout(Principal principal, @PathVariable Long listId, @PathVariable Long layoutId) {
        this.shoppingListService.changeListLayout(principal.getName(), listId, layoutId);

        return ResponseEntity.noContent().build();
    }


    private ResponseEntity<ShoppingListResource> singleResult(ShoppingListEntity result, List<Category> categories) {
        if (result != null) {

            ShoppingListResource shoppingListResource = new ShoppingListResource(result, categories);

            return new ResponseEntity(shoppingListResource, HttpStatus.OK);
        }
        return ResponseEntity.noContent().build();
    }


}
