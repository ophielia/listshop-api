/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Tag;

import java.util.List;


public class ListLayoutCategory {

    String name;

    @JsonProperty("category_id")
    private String id;

    @JsonProperty("display_order")
    int displayOrder;

    @JsonProperty("tags")
    private List<NestedTag> tags;

    @JsonProperty("is_default")
    private boolean isDefault;

    public ListLayoutCategory() {
    }

    public ListLayoutCategory(String id) {
        this.id = id;
    }

    public ListLayoutCategory(Long id) {
        this.id = String.valueOf(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        if (displayOrder == null) {
            this.displayOrder = 0;
            return;
        }
        this.displayOrder = displayOrder;
    }

    public List<NestedTag> getTags() {
        return tags;
    }

    public void setTags(List<NestedTag> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
