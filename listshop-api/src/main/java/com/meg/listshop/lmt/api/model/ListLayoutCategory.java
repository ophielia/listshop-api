package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ListLayoutCategory {

    String name;

    @JsonProperty("category_id")
    private Long id;

    int displayOrder;

    @JsonProperty("subcategories")
    private List<ListLayoutCategory> subCategories = new ArrayList<>();

    @JsonProperty("tags")
    private List<Tag> tags;

    public ListLayoutCategory() {
    }

    public ListLayoutCategory(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<ListLayoutCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<ListLayoutCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}