/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public ShoppingListCategory(String categoryId, String name, int displayOrder, List<ShoppingListItem> items) {
        this.categoryId = categoryId;
        this.displayOrder = displayOrder;
        this.items = items;
        this.name = name;
    }
}
