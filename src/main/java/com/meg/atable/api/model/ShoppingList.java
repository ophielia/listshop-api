package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ShoppingList {

    private Long list_id;

    @JsonProperty("created")
    private Date createdOn;

    @JsonProperty("list_type")
    private String listType;

    @JsonProperty("layout_type")
    private String layoutType;

    private java.util.List<Category>
            categories;

    @JsonProperty("user_id")
    private Long userId;

    public ShoppingList() {
        // empty constructor
    }


    public ShoppingList(Long id) {
        this.list_id = id;
    }

    public Long getList_id() {
        return list_id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public ShoppingList createdOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public String getListType() {
        return listType;
    }

    public ShoppingList listType(String listType) {
        this.listType = listType;
        return this;
    }

    public java.util.List<Category> getCategories() {
        return categories;
    }

    public ShoppingList categories(java.util.List<Category> categories) {
        this.categories = categories;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public ShoppingList userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public ShoppingList layoutType(String layoutType) {
        this.layoutType = layoutType;
        return this;
    }
}