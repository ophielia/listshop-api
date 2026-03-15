/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class ListLayout {
    @JsonProperty("layout_id")
    private String layoutId;

    private String name;

    @JsonProperty(value="is_default")
    private boolean isDefault;

    @JsonProperty(value="user_id")
    private String userId;

    private List<ListLayoutCategory> categories;

    public ListLayout(String layoutId) {
        this.layoutId = layoutId;
    }
    public ListLayout(Long layoutId) {
        this.layoutId = String.valueOf(layoutId);
    }

    public ListLayout() {
        // empty constructor for jpa
    }


    public String getLayoutId() {
        return layoutId;
    }

    public String getName() {
        return name;
    }

    public ListLayout withName(String name) {
        this.name = name;
        return this;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public ListLayout withDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public List<ListLayoutCategory> getCategories() {
        return categories;
    }

    public ListLayout withCategories(List<ListLayoutCategory> categories) {
        this.categories = categories;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ListLayout withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        return "ListLayout{" +
                "layoutId=" + layoutId +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                ", userId='" + userId + '\'' +
                ", categories=" + categories +
                '}';
    }
}
