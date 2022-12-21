package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class ListLayout {
    private Long layoutId;

    private String name;

    @JsonProperty(value="is_default")
    private boolean isDefault;

    private List<ListLayoutCategory> categories;

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

    @JsonIgnore
    public boolean isDefault() {
        return isDefault;
    }

    @JsonIgnore
    public ListLayout isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public List<ListLayoutCategory> getCategories() {
        return categories;
    }

    public ListLayout categories(List<ListLayoutCategory> categories) {
        this.categories = categories;
        return this;
    }
}
