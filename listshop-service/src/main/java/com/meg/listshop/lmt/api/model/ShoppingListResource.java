package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.categories.ListShopCategory;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;


/**
 * Created by margaretmartin on 30/10/2017.
 */
public class ShoppingListResource extends RepresentationModel {

    @JsonProperty("shopping_list")
    private ShoppingList shoppingList;

    public ShoppingListResource(ShoppingListEntity shoppingListEntity, List<ListShopCategory> categories) {
        this.shoppingList = ModelMapper.toModel(shoppingListEntity, categories);

        Long userId = shoppingListEntity.getUserId();
        // add link to all shopping lists for user
        //  this.add(linkTo(ShoppingListRestController.class, userId).withRel("shoppinglist"));
        // add link to this shopping list
      /*  this.add(linkTo(methodOn(ShoppingListRestController.class, userId)
                .retrieveListById(null, shoppingListEntity.getId(),0L)).withSelfRel()); */
        //this.add(linkTo(ShoppingListRestController.class).slash(shoppingListEntity.getId()).withSelfRel());
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public ShoppingListResource() {
    }

    @JsonIgnore
    public Links getLinks() {
        return super.getLinks();
    }
}
