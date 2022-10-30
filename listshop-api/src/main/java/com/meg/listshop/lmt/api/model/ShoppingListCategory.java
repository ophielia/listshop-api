package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShoppingListCategory {

    String name;

    @JsonProperty("category_id")
    private Long id;

    @JsonProperty("user_category_id")
    private Long userCategoryId;

    @JsonProperty("displayOrder")
    int displayOrder;

    @JsonProperty("items")
    private List<Item> items;

    public ShoppingListCategory() {
    }

    public ShoppingListCategory(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserCategoryId() {
        return userCategoryId;
    }

    public void setUserCategoryId(Long userCategoryId) {
        this.userCategoryId = userCategoryId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setShoppingListItems(List<ShoppingListItem> shoppingListItems) {
        //MM layout fill in - sidecar for replacing items
    }
}