package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("shopping_list")
    private ShoppingList shoppingList;

    public ShoppingListResource() {
        // empty constructor for jackson
    }

    public ShoppingListResource(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "tag";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return String.valueOf(shoppingList.getList_id());
    }
}