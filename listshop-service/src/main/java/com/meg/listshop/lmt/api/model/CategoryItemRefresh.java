package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.categories.ListShopCategory;

public class CategoryItemRefresh {

    @JsonProperty("tag")
    private Tag tag;

    @JsonProperty("category")
    private ListShopCategory category;

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

    public ListShopCategory getCategory() {
        return category;
    }

    public void setCategory(ListShopCategory category) {
        this.category = category;
    }
}