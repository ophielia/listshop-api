/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class NestedShoppingList {

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("created")
    private Date createdOn;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("is_starter_list")
    private Boolean isStarterList;

    @JsonProperty("name")
    private String name;

    public NestedShoppingList() {
        // empty constructor
    }

    public NestedShoppingList(String listId) {
        this.listId = listId;
    }
    public NestedShoppingList(Long listId) {
        this.listId = String.valueOf(listId);
    }

    public NestedShoppingList withListId(String listId) {
        this.listId = listId;
        return this;
    }

    public NestedShoppingList withCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public NestedShoppingList withUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public NestedShoppingList withItemCount(Integer itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public NestedShoppingList withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public NestedShoppingList withIsStarterList(Boolean isStarterList) {
        this.isStarterList = isStarterList;
        return this;
    }

    public NestedShoppingList withName(String name) {
        this.name = name;
        return this;
    }

}

