package com.meg.atable.controller;

import com.meg.atable.api.controller.ShoppingListRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Autowired
    private UserService userService;

    public ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal) {

        List<ShoppingListResource> shoppingListResources = shoppingListService
                .getListsByUsername(principal.getName())
                .stream()
                .map(ShoppingListResource::new)
                .collect(Collectors.toList());
        Resources<ShoppingListResource> listResourceList = new Resources<>(shoppingListResources);
        return new ResponseEntity(listResourceList, HttpStatus.OK);
    }

    //@RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> createList(Principal principal, @RequestBody ShoppingList shoppingList) {
        ShoppingListEntity shoppingListEntity = ModelMapper.toEntity(shoppingList);

        ShoppingListEntity result = shoppingListService.createList(principal.getName(), shoppingListEntity);

        if (result != null) {
            Link oneList = new ShoppingListResource(result).getLink("self");
            return ResponseEntity.created(URI.create(oneList.getHref())).build();
        }
        return ResponseEntity.badRequest().build();

    }

    //@RequestMapping(method = RequestMethod.GET, value="/type/{listType}", produces = "application/json")
    public ResponseEntity<ShoppingListResource> retrieveListByType(Principal principal, @PathVariable("listType") String listTypeString) {
        ListType listType = ListType.valueOf(listTypeString);
        ShoppingListEntity result = shoppingListService.getListByUsernameAndType(principal.getName(), listType);

        return singleResult(result);

    }

    //@RequestMapping(method = RequestMethod.GET, value = "/{listId}", produces = "application/json")
    public ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId) {
        ShoppingListEntity result = shoppingListService.getListById(principal.getName(), listId);

        return singleResult(result);
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
    public ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId) {
        this.shoppingListService.deleteItemFromList(principal.getName(), listId, itemId);

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ShoppingListResource> singleResult(ShoppingListEntity result) {
        if (result != null) {
            ShoppingListResource shoppingListResource = new ShoppingListResource(result);

            return new ResponseEntity(shoppingListResource, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }


}
