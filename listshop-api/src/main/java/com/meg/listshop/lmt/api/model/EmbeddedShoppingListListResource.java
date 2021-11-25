package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedShoppingListListResource {

    @JsonProperty("shoppingListResourceList")
    private List<ShoppingListResource> shoppingListList;

    public EmbeddedShoppingListListResource(List<ShoppingListResource> shoppingListList) {
        this.shoppingListList = shoppingListList;
    }


}