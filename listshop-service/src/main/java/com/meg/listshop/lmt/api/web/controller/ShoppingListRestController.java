package com.meg.listshop.lmt.api.web.controller;

import com.google.common.base.Enums;
import com.meg.listshop.lmt.api.controller.ShoppingListRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.ShoppingListException;
import com.meg.listshop.lmt.service.ShoppingListService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ShoppingListRestController implements ShoppingListRestControllerApi {

    private static final Logger logger = LogManager.getLogger(ShoppingListRestController.class);

    private final ShoppingListService shoppingListService;

    @Autowired
    public ShoppingListRestController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    public ResponseEntity<ShoppingListListResource> retrieveLists(HttpServletRequest request, Principal principal) {
        String message = String.format("Retrieving all lists for user [%S]", principal.getName());
        logger.info(message);
        List<ShoppingListResource> shoppingListList = shoppingListService
                .getListsByUsername(principal.getName())
                .stream()
                .map(t -> ModelMapper.toModel(t, null))
                .map(ShoppingListResource::new)
                .collect(Collectors.toList());

        ShoppingListListResource resource = new ShoppingListListResource(shoppingListList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> createList(HttpServletRequest request, Principal principal, @RequestBody ListGenerateProperties listGenerateProperties) {
        String message = String.format("Creating list for user [%S]", principal.getName());
        logger.info(message);

        ShoppingListEntity result = null;
        try {
            result = shoppingListService.generateListForUser(principal.getName(), listGenerateProperties);
        } catch (ShoppingListException e) {
            logger.error("Exception while creating List.", e);
        }
        if (result != null) {
            ShoppingListResource resource = new ShoppingListResource(ModelMapper.toModel(result, null));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.badRequest().build();
    }


    @Override
    public ResponseEntity<MergeResultResource> mergeList(Principal principal, @RequestBody MergeRequest mergeRequest) {
        String message = String.format("Merging list for user [%S]", principal.getName());
        logger.info(message);

        Long listId = mergeRequest.getListId();
        Long layoutId = mergeRequest.getLayoutId();

        MergeResult mergeResult = this.shoppingListService.mergeFromClient(principal.getName(), mergeRequest);

        // check for conflicts (won't be any until we implement this)
        if (mergeResult.getMergeConflicts() == null) {
            // retrieve the list, and put it into the result
            ShoppingListEntity shoppingList = this.shoppingListService.getListById(principal.getName(), listId);
            // possibly set layout id in shopping list
            if (layoutId != null && !layoutId.equals(shoppingList.getListLayoutId())) {
                shoppingList.setListLayoutId(layoutId);
            }
            List<ShoppingListCategory> categories = shoppingListService.categorizeList(shoppingList);
            shoppingListService.fillSources(shoppingList);
            mergeResult.setShoppingList(ModelMapper.toModel(shoppingList, categories));
            MergeResultResource resource = new MergeResultResource(mergeResult);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> updateList(HttpServletRequest request, Principal principal, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList) {
        ShoppingListEntity updateFrom = ModelMapper.toEntity(shoppingList);

        ShoppingListEntity result = shoppingListService.updateList(principal.getName(), listId, updateFrom);
        if (result != null) {
            ShoppingListResource resource = new ShoppingListResource(ModelMapper.toModel(result, null));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.ok(URI.create(link));
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> updateItems(Principal principal, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation) {
        String message = String.format("beginning updateItems for input: %S", itemOperation);
        logger.debug(message);
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

    public ResponseEntity<ShoppingListResource> retrieveMostRecentList(HttpServletRequest request, Principal principal) {
        ShoppingListEntity result = shoppingListService.getMostRecentList(principal.getName());
        if (result == null) {
            throw new ObjectNotFoundException("No lists found for user [" + principal.getName() + "] in retrieveMostRecentList()");
        }
        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }

    public ResponseEntity<ShoppingListResource> retrieveStarterList(HttpServletRequest request, Principal principal) {
        ShoppingListEntity result = shoppingListService.getStarterList(principal.getName());
        if (result == null) {
            throw new ObjectNotFoundException("No lists found for user [" + principal.getName() + "] in retrieveStarterList()");
        }
        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }

    @Override
    public ResponseEntity<ShoppingListResource> retrieveListById(HttpServletRequest request, Principal principal, @PathVariable("listId") Long listId) {
        ShoppingListEntity result = shoppingListService.getListById(principal.getName(), listId);

        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }


    @Override
    public ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId) {
        shoppingListService.deleteList(principal.getName(), listId);
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> updateItemCountByTag(Principal principal, @PathVariable Long listId,
                                                       @PathVariable Long tagId,
                                                       @PathVariable Integer usedCount
    ) {
        final String message = String.format("Update count for tag [%d] to [%d] in list [%d]", tagId, usedCount, listId);
        logger.info(message);
        this.shoppingListService.updateItemCount(principal.getName(), listId, tagId, usedCount);
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
    public ResponseEntity<Object> generateListFromMealPlan(HttpServletRequest request, Principal principal, @PathVariable Long mealPlanId) {
        ShoppingListEntity shoppingListEntity = this.shoppingListService.generateListFromMealPlan(principal.getName(), mealPlanId);
        if (shoppingListEntity != null) {
            ShoppingListResource resource = new ShoppingListResource(ModelMapper.toModel(shoppingListEntity, null));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addToListFromMealPlan(Principal principal, @PathVariable Long listId, @PathVariable Long mealPlanId) {
        this.shoppingListService.addToListFromMealPlan(principal.getName(), listId, mealPlanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{listId}/dish", produces = "application/json")
    public ResponseEntity<Object> addDishesToList(Principal principal, @PathVariable Long listId, @RequestBody ListAddProperties listAddProperties) {
        String message = String.format("Adding dishes to list for user [%S]", principal.getName());
        logger.info(message);

        try {
            shoppingListService.addDishesToList(principal.getName(), listId, listAddProperties);
        } catch (ShoppingListException e) {
            logger.error("Exception while creating List.", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<Object> addDishToList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId) {
        try {
            this.shoppingListService.addDishToList(principal.getName(), listId, dishId);
        } catch (ShoppingListException s) {
            logger.error("Unable to add Dish [%d] to List [%d]", s, dishId, listId);
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


    private ResponseEntity<ShoppingListResource> singleResult(HttpServletRequest request, ShoppingListEntity result, List<ShoppingListCategory> categories) {
        if (result != null) {

            ShoppingList shoppingList = ModelMapper.toModel(result, categories);
            ShoppingListResource resource = new ShoppingListResource(shoppingList);
            resource.fillLinks(request, resource);
            return new ResponseEntity<>(resource, HttpStatus.OK);
        }
        return ResponseEntity.noContent().build();
    }


}
