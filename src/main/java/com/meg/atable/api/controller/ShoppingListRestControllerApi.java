package com.meg.atable.api.controller;

import com.meg.atable.api.model.Item;
import com.meg.atable.api.model.ShoppingList;
import com.meg.atable.api.model.ShoppingListResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/shoppinglist")
@CrossOrigin
public interface ShoppingListRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createList(Principal principal, @RequestBody ShoppingList shoppingList);

    @RequestMapping(method = RequestMethod.GET, value = "/type/{listType}", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveListByType(Principal principal, @PathVariable("listType") String listType);

    @RequestMapping(method = RequestMethod.GET, value = "/{listId}", produces = "application/json")
    public ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}", produces = "application/json")
    public ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/item", produces = "application/json")
    public ResponseEntity<Object> addItemToList(Principal principal, @PathVariable Long listId, @RequestBody Item input);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/item/{itemId}", produces = "application/json")
    public ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId);

}
