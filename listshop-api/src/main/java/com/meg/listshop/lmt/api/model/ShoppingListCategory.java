package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShoppingListCategory {

    String name;

    @JsonProperty("category_id")
    private Long id;

    @JsonIgnore
    private Long userCategoryId;

    @JsonProperty("displayOrder")
    int displayOrder;

    @JsonProperty("items")
    private List<ShoppingListItem> items;

    @JsonIgnore
    private int userDisplayOrder;

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
        if (this.id != null && this.userCategoryId != null) {
            return;
        }
        this.id = id;
    }

    public Long getUserCategoryId() {
        return userCategoryId;
    }

    public void setUserCategoryId(Long userCategoryId) {
        if (userCategoryId == null) {
            return;
        }
        this.userCategoryId = userCategoryId;
        this.id = userCategoryId;
    }

    public int getUserDisplayOrder() {
        return userDisplayOrder;
    }

    public void setUserDisplayOrder(int userDisplayOrder) {
        if (userDisplayOrder == 0) {
            return;
        }
        this.userDisplayOrder = userDisplayOrder;
        this.displayOrder = userDisplayOrder;
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingListItem> items) {
        this.items = items;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder == 0 || (this.displayOrder > 0 && this.userDisplayOrder > 0)) {
            return;
        }
        this.displayOrder = displayOrder;
    }

}