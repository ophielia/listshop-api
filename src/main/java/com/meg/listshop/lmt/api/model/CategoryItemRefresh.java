package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

public class CategoryItemRefresh {

    @JsonProperty("tag")
    private Tag tag;

    @JsonProperty("category")
    private Category category;

    public CategoryItemRefresh(TagEntity tag, ListLayoutCategoryEntity category) {
        this.tag = ModelMapper.toModel(tag);
        this.category = ModelMapper.toModel(category, false);
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}