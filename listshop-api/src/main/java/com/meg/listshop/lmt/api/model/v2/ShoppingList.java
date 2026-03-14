/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingList {

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("created")
    private Date createdOn;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("layout_id")
    private String layoutId;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("legend")
    private List<LegendSource> legendSources;

    @JsonProperty("categories")
    private List<ShoppingListCategory>
            categories;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("is_starter_list")
    private Boolean isStarterList;

    @JsonProperty("name")
    private String name;

    public ShoppingList() {
        // empty constructor
    }

    public ShoppingList(String listId) {
        this.listId = listId;
    }


    public ShoppingList(Long listId) {
        this.listId = String.valueOf(listId);
    }

    public ShoppingList withListId(String listId) {
        this.listId = listId;
        return this;
    }

    public ShoppingList withCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public ShoppingList withUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public ShoppingList withLayoutId(String layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public ShoppingList withItemCount(Integer itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public ShoppingList withLegendSources(List<LegendSource> legendSources) {
        this.legendSources = legendSources;
        return this;
    }

    public ShoppingList withCategories(List<ShoppingListCategory> categories) {
        this.categories = categories;
        return this;
    }

    public ShoppingList withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ShoppingList withIsStarterList(Boolean isStarterList) {
        this.isStarterList = isStarterList;
        return this;
    }

    public ShoppingList withName(String name) {
        this.name = name;
        return this;
    }

}

