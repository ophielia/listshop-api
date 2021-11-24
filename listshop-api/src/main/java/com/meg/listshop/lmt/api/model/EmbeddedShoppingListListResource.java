package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedShoppingListListResource {

    @JsonProperty("shoppingListResourceList")
    private List<ShoppingList> shoppingListList;

    public EmbeddedShoppingListListResource(List<ShoppingList> shoppingListList) {
        this.shoppingListList = shoppingListList;
    }


}