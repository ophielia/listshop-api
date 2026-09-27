/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingListPut {

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("is_starter_list")
    private Boolean isStarterList;

    @JsonProperty("name")
    private String name;

    public ShoppingListPut() {
        // empty constructor
    }


    public ShoppingListPut(String id) {
        this.listId = id;
    }

    public String getListId() {
        return listId;
    }

    @JsonProperty("is_starter_list")
    public Boolean getStarterList() {
        return isStarterList;
    }

    public ShoppingListPut isStarterList(Boolean starterList) {
        isStarterList = starterList;
        return this;
    }

    public String getName() {
        return name;
    }

    public ShoppingListPut name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "ShoppingListPut{" +
                "listId=" + listId +
                ", isStarterList=" + isStarterList +
                ", name='" + name + '\'' +
                '}';
    }
}
