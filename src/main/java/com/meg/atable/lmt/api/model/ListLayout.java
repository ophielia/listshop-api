package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class ListLayout {
    private Long layoutId;

    private String name;

    @JsonProperty("list_layout_type")
    private String layoutType;

    private List<Category> categories;

    public ListLayout(Long layoutId) {
        this.layoutId = layoutId;
    }

    public ListLayout() {
        // empty constructor for jpa
    }

    @JsonProperty("layout_id")
    public Long getLayoutId() {
        return layoutId;
    }

    public String getName() {
        return name;
    }

    public ListLayout name(String name) {
        this.name = name;
        return this;
    }


    public String getLayoutType() {
        return layoutType;
    }

    public ListLayout layoutType(String layoutType) {
        this.layoutType = layoutType;
        return this;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ListLayout categories(List<Category> categories) {
        this.categories = categories;
        return this;
    }
}
