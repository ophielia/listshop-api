package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingListPut {

    private Long list_id;

    @JsonProperty("is_starter_list")
    private Boolean isStarterList;

    @JsonProperty("name")
    private String name;

    public ShoppingListPut() {
        // empty constructor
    }


    public ShoppingListPut(Long id) {
        this.list_id = id;
    }

    public Long getList_id() {
        return list_id;
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
}