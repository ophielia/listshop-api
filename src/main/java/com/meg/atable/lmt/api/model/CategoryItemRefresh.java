package com.meg.atable.lmt.api.model;

import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

public class CategoryItemRefresh {

    private Tag tag;

    private Category category;

    public CategoryItemRefresh(TagEntity tag, ListLayoutCategoryEntity category) {
        this.tag = ModelMapper.toModel(tag);
        this.category = ModelMapper.toModel(category);
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