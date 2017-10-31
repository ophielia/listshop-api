package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.data.entity.ItemEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;

public class ShoppingList {

    private Long list_id;

    @JsonProperty("created")
    private Date createdOn;

    @JsonProperty("list_type")
    private ListType listType;

    private java.util.List<Category>
            categories;

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

    public void setList_id(Long list_id) {
        this.list_id = list_id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public ListType getListType() {
        return listType;
    }

    public void setListType(ListType listType) {
        this.listType = listType;
    }

    public java.util.List<Category> getCategories() {
        return categories;
    }

    public void setCategories(java.util.List<Category> categories) {
        this.categories = categories;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}