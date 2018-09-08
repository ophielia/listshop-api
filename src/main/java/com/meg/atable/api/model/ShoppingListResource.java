package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.web.controller.ShoppingListRestController;
import com.meg.atable.data.entity.ShoppingListEntity;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public class ShoppingListResource extends ResourceSupport {

    @JsonProperty("shopping_list")
    private final ShoppingList shoppingList;

    public ShoppingListResource(ShoppingListEntity shoppingListEntity, List<Category> categories) {
        this.shoppingList = ModelMapper.toModel(shoppingListEntity, categories);

        Long userId = shoppingListEntity.getUserId();
        // add link to all shopping lists for user
        this.add(linkTo(ShoppingListRestController.class, userId).withRel("shoppinglist"));
        // add link to this shopping list
      /*  this.add(linkTo(methodOn(ShoppingListRestController.class, userId)
                .retrieveListById(null, shoppingListEntity.getId(),0L)).withSelfRel()); */
        this.add(linkTo(ShoppingListRestController.class).slash(shoppingListEntity.getId()).withSelfRel());
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }
}
