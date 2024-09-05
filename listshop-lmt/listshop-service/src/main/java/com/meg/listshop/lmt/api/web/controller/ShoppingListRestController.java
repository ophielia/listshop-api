package com.meg.listshop.lmt.api.web.controller;

import com.google.common.base.Enums;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.lmt.api.controller.ShoppingListRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.ShoppingListException;
import com.meg.listshop.lmt.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ShoppingListRestController implements ShoppingListRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingListRestController.class);

    private final ShoppingListService shoppingListService;

    @Autowired
    public ShoppingListRestController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    public ResponseEntity<ShoppingListListResource> retrieveLists(HttpServletRequest request, Authentication authentication) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();

        String message = String.format("Retrieving all lists for user [%S]", userDetails.getId());
        logger.info(message);
        List<ShoppingListResource> shoppingListList = shoppingListService
                .getListsByUserId(userDetails.getId())
                .stream()
                .map(t -> ModelMapper.toModel(t, null))
                .map(ShoppingListResource::new)
                .collect(Collectors.toList());

        ShoppingListListResource resource = new ShoppingListListResource(shoppingListList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> createList(HttpServletRequest request, Authentication authentication, @RequestBody ListGenerateProperties listGenerateProperties) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("Creating list for user [%S]", authentication.getName());
        logger.info(message);

        ShoppingListEntity result = null;
        try {
            result = shoppingListService.generateListForUser(userDetails.getId(), listGenerateProperties);
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
    public ResponseEntity<MergeResultResource> mergeList(Authentication authentication, @RequestBody MergeRequest mergeRequest) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("Merging list for user [%S]", userDetails.getId());
        logger.info(message);

        Long listId = mergeRequest.getListId();
        Long layoutId = mergeRequest.getLayoutId();

        MergeResult mergeResult = this.shoppingListService.mergeFromClient(userDetails.getId(), mergeRequest);

        // check for conflicts (won't be any until we implement this)
        if (mergeResult.getMergeConflicts() == null) {
            // retrieve the list, and put it into the result
            ShoppingListEntity shoppingList = this.shoppingListService.getListForUserById(userDetails.getId(), listId);
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
    public ResponseEntity<Object> updateList(HttpServletRequest request, Authentication authentication, @PathVariable("listId") Long listId, @RequestBody ShoppingListPut shoppingList) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        final String message = String.format("Updating list for list [%d]", listId);
        logger.info(message);
        ShoppingListEntity updateFrom = ModelMapper.toEntity(shoppingList);

        ShoppingListEntity result = shoppingListService.updateList(userDetails.getId(), listId, updateFrom);
        if (result != null) {
            ShoppingListResource resource = new ShoppingListResource(ModelMapper.toModel(result, null));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.ok(URI.create(link));
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> updateItems(Authentication authentication, @PathVariable("listId") Long listId, @RequestBody ItemOperationPut itemOperation) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
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
        shoppingListService.performItemOperation(userDetails.getId(), listId, operationType, tagIdsForUpdate, destinationListId);
        // return
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ShoppingListResource> retrieveMostRecentList(HttpServletRequest request, Authentication authentication) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Retrieving most recent list for user {}", userDetails.getId());
        ShoppingListEntity result = shoppingListService.getMostRecentList(userDetails.getId());
        if (result == null) {
            throw new ObjectNotFoundException(String.format("No lists found for user [%s] in retrieveMostRecentList()", userDetails.getId()));
        }
        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }

    public ResponseEntity<ShoppingListResource> retrieveStarterList(HttpServletRequest request, Authentication authentication) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Retrieving starter list for user {}", userDetails.getId());
        ShoppingListEntity result = shoppingListService.getStarterList(userDetails.getId());
        if (result == null) {
            throw new ObjectNotFoundException(String.format("No lists found for user [%s] in retrieveStarterList()", userDetails.getId()));
        }
        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }

    @Override
    public ResponseEntity<ShoppingListResource> retrieveListById(HttpServletRequest request, Authentication authentication, @PathVariable("listId") Long listId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Retrieving list [{}] by id for user [{}]", listId, userDetails.getId());
        ShoppingListEntity result = shoppingListService.getListForUserById(userDetails.getId(), listId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        List<ShoppingListCategory> categories = shoppingListService.categorizeList(result);
        shoppingListService.fillSources(result);
        return singleResult(request, result, categories);
    }


    @Override
    public ResponseEntity<ShoppingList> deleteList(Authentication authentication, @PathVariable("listId") Long listId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Deleting list [{}] for user [{}]", listId, userDetails.getId());
        shoppingListService.deleteList(userDetails.getId(), listId);
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> updateItemCountByTag(Authentication authentication, @PathVariable Long listId,
                                                       @PathVariable Long tagId,
                                                       @PathVariable Integer usedCount
    ) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        final String message = String.format("Update count for tag [%d] to [%d] in list [%d]", tagId, usedCount, listId);
        logger.info(message);
        this.shoppingListService.updateItemCount(userDetails.getId(), listId, tagId, usedCount);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> addItemToListByTag(Authentication authentication, @PathVariable Long listId, @PathVariable Long tagId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Adding tag [{}] to list [{}] for user [{}]", tagId, listId, userDetails.getId());
        this.shoppingListService.addItemToListByTag(userDetails.getId(), listId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteItemFromList(Authentication authentication, @PathVariable Long listId, @PathVariable Long itemId,
                                                     @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                                     @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Deleting item [{}] from list [{}] for user [{}]", itemId, listId, userDetails.getId());
        Long serviceSourceId = null;
        if (!"0".equals(sourceId)) {
            serviceSourceId = Long.valueOf(sourceId);
        }

        this.shoppingListService.deleteItemFromList(userDetails.getId(), listId, itemId, removeEntireItem, serviceSourceId);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> setCrossedOffForItem(Authentication authentication, @PathVariable Long listId, @PathVariable Long itemId,
                                                       @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    ) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Setting crossed off for item [{}] on list [{}] for user [{}]", itemId, listId, userDetails.getId());
        this.shoppingListService.updateItemCrossedOff(userDetails.getId(), listId, itemId, crossedOff);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> crossOffAllItemsOnList(Authentication authentication, @PathVariable Long listId,
                                                         @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Setting crossed off [{}] for all items on list [{}] for user [{}]", crossedOff, listId, userDetails.getId());
        this.shoppingListService.crossOffAllItems(userDetails.getId(), listId, crossedOff);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> deleteAllItemsFromList(Authentication authentication, @PathVariable Long listId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Deleting all items from list [{}] for user [{}]", listId, userDetails.getId());
        this.shoppingListService.deleteAllItemsFromList(userDetails.getId(), listId);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> generateListFromMealPlan(HttpServletRequest request, Authentication authentication, @PathVariable Long mealPlanId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Generating list from mealplan [{}] for user [{}]", mealPlanId, userDetails.getId());
        ShoppingListEntity shoppingListEntity = this.shoppingListService.generateListFromMealPlan(userDetails.getId(), mealPlanId);
        if (shoppingListEntity != null) {
            ShoppingListResource resource = new ShoppingListResource(ModelMapper.toModel(shoppingListEntity, null));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addToListFromMealPlan(Authentication authentication, @PathVariable Long listId, @PathVariable Long mealPlanId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Adding to list [{}] from meal plan [{}] for user [{}]", listId, mealPlanId, userDetails.getId());
        this.shoppingListService.addToListFromMealPlan(userDetails.getId(), listId, mealPlanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{listId}/dish", produces = "application/json")
    public ResponseEntity<Object> addDishesToList(Authentication authentication, @PathVariable Long listId, @RequestBody ListAddProperties listAddProperties) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("Adding dishes to list for user [%S]", userDetails.getId());
        logger.info(message);

        try {
            shoppingListService.addDishesToList(userDetails.getId(), listId, listAddProperties);
        } catch (ShoppingListException e) {
            logger.error("Exception while creating List.", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<Object> addDishToList(Authentication authentication, @PathVariable Long listId, @PathVariable Long dishId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Adding dish [{}] to list [{}] for user [{}]", dishId, listId, userDetails.getId());
        try {
            this.shoppingListService.addDishToList(userDetails.getId(), listId, dishId);
        } catch (ShoppingListException s) {
            logger.error("Unable to add Dish [{}] to List [{}]", dishId, listId);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> removeDishFromList(Authentication authentication, @PathVariable Long listId, @PathVariable Long dishId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Removing dish [{}] from list [{}] for user [{}]", dishId, listId, userDetails.getId());
        this.shoppingListService.removeDishFromList(userDetails.getId(), listId, dishId);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addToListFromList(Authentication authentication, @PathVariable Long listId, @PathVariable Long fromListId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Adding list [{}] to list [{}] for user [{}]", fromListId, listId, userDetails.getId());
        this.shoppingListService.addListToList(userDetails.getId(), listId, fromListId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> removeFromListByList(Authentication authentication, @PathVariable Long listId, @PathVariable Long fromListId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Removing list [{}] from list [{}] for user [{}]", fromListId, listId, userDetails.getId());
        this.shoppingListService.removeListItemsFromList(userDetails.getId(), listId, fromListId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> changeListLayout(Authentication authentication, @PathVariable Long listId, @PathVariable Long layoutId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Chaning list layout [{}] for list [{}] for user [{}]", layoutId, listId, userDetails.getId());
        this.shoppingListService.changeListLayout(userDetails.getId(), listId, layoutId);

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
