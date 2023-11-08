package com.meg.listshop.lmt.service;


import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class TagTestBuilder {
    private final TagEntity tag;

    private List<ListLayoutCategoryEntity> categories = new ArrayList<>();


    public TagTestBuilder() {
        tag = new TagEntity();
    }

    public TagTestBuilder withTagId(Long tagId) {
        tag.setId(tagId);
        tag.setName(tagId.toString());
        return this;
    }

    public TagTestBuilder withUserId(Long userId) {
        tag.setUserId(userId);
        return this;
    }


    public TagTestBuilder withName(String tagName) {
        tag.setName(tagName);
        return this;
    }


    public TagTestBuilder withCategory(ListLayoutCategoryEntity category) {
        if (category != null) {
            categories.add(category);
        }
        return this;
    }

    public TagEntity build() {
        tag.setCategories(categories);
        return tag;
    }




}