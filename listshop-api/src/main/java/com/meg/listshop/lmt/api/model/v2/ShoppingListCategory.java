package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.ShoppingListItem;

import java.util.List;

public class ShoppingListCategory {

    @JsonProperty("name")
    String name;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("display_order")
    int displayOrder;

    @JsonProperty("items")
    private List<ShoppingListItem> items;

    public ShoppingListCategory() {
        // empty constructor for Jackson
    }

    public ShoppingListCategory(String categoryId, int displayOrder, List<ShoppingListItem> items) {
        this.categoryId = categoryId;
        this.displayOrder = displayOrder;
        this.items = items;
    }
}
