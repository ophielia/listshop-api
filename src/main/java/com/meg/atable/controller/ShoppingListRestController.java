package com.meg.atable.controller;

import com.meg.atable.api.controller.ShoppingListRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.service.ShoppingListException;
import com.meg.atable.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ShoppingListRestController implements ShoppingListRestControllerApi {

    @Autowired
    private ShoppingListService shoppingListService;

    public ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal) {

        List<ShoppingListResource> shoppingListResources = shoppingListService
                .getListsByUsername(principal.getName())
                .stream()
                .map(t -> new ShoppingListResource(t, null))
                .collect(Collectors.toList());
        Resources<ShoppingListResource> listResourceList = new Resources<>(shoppingListResources);
        return new ResponseEntity(listResourceList, HttpStatus.OK);
    }

    //@RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> createList(Principal principal, @RequestBody ShoppingList shoppingList) {
        ShoppingListEntity shoppingListEntity = ModelMapper.toEntity(shoppingList);

        ShoppingListEntity result = shoppingListService.createList(principal.getName(), shoppingListEntity);

        if (result != null) {
            Link oneList = new ShoppingListResource(result, null).getLink("self");
            return ResponseEntity.created(URI.create(oneList.getHref())).build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> setListActive(Principal principal, @PathVariable("listId") Long listId, @RequestParam(value = "generateType", required = true) String filter) {
        GenerateType generateType = GenerateType.valueOf(filter);

        ShoppingListEntity result = shoppingListService.setListActive(principal.getName(), listId, generateType);
        if (result != null) {

            Link oneList = new ShoppingListResource(result, null).getLink("self");
            return ResponseEntity.created(URI.create(oneList.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    //@RequestMapping(method = RequestMethod.GET, value="/type/{listType}", produces = "application/json")
    public ResponseEntity<ShoppingListResource> retrieveListByType(Principal principal, @PathVariable("listType") String listTypeString) {
        ListType listType = ListType.valueOf(listTypeString);
        ShoppingListEntity result = shoppingListService.getListByUsernameAndType(principal.getName(), listType);

        return singleResult(result, null);

    }

    //@RequestMapping(method = RequestMethod.GET, value = "/{listId}", produces = "application/json")
    @Override
    public ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId,
                                                                     @RequestParam(value="highlightDish", required=false,defaultValue="0") Long highlightDish,
                                                                     @RequestParam(value="showPantry", required=false,defaultValue="false") Boolean showPantry) {
        ShoppingListEntity result = shoppingListService.getListById(principal.getName(), listId);

        if (highlightDish.equals(0)) {
            highlightDish = null;
        }
        List<Category> categories = shoppingListService.categorizeList(result,highlightDish,showPantry );
        shoppingListService.fillSources(result);
        return singleResult(result, categories);
    }

    //@RequestMapping(method = RequestMethod.DELETE, value = "/{listId}", produces = "application/json")
    public ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId) {
        boolean success = shoppingListService.deleteList(principal.getName(), listId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{listId}/item", produces = "application/json")
    public ResponseEntity<Object> addItemToList(Principal principal, @PathVariable Long listId, @RequestBody Item input) {
        ItemEntity itemEntity = ModelMapper.toEntity(input);

        this.shoppingListService.addItemToList(principal.getName(), listId, itemEntity);

        return ResponseEntity.noContent().build();
    }

    // @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/item/{itemId}", produces = "application/json")
    public ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                                     @RequestParam(value="removeEntireItem", required=false,defaultValue="false") Boolean removeEntireItem,
                                                     @RequestParam(value="sourceId", required=false,defaultValue="0") Long sourceId) {
        if (sourceId.equals(0)) {
            sourceId = null;
        }

        this.shoppingListService.deleteItemFromList(principal.getName(), listId, itemId, removeEntireItem, sourceId);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> generateListFromMealPlan(Principal principal, @PathVariable Long mealPlanId) {
        ShoppingListEntity shoppingListEntity = this.shoppingListService.generateListFromMealPlan(principal.getName(), mealPlanId);
        if (shoppingListEntity != null) {
            Link listLink = new ShoppingListResource(shoppingListEntity, null).getLink("self");
            return ResponseEntity.created(URI.create(listLink.getHref())).build();

        }
        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{listId}/dish/{dishId}", produces = "application/json")
    @Override
    public ResponseEntity<Object> addDishToList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId) {
        try {
            this.shoppingListService.addDishToList(principal.getName(), listId, dishId);
        } catch (ShoppingListException s) {
            // MM TODO LOGGING
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/dish/{dishId}", produces = "application/json")
    @Override
    public ResponseEntity<Object> removeDishFromList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId) {
            this.shoppingListService.removeDishFromList(principal.getName(), listId, dishId);

        return ResponseEntity.noContent().build();
    }


    //@RequestMapping(method = RequestMethod.POST, value = "/{listId}/layout/{layoutId}", produces = "application/json")
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
