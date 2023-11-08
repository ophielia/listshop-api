package com.meg.listshop.lmt.service;


import com.meg.listshop.lmt.api.model.Dish;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class DishTestBuilder {
    private final DishEntity dish;

    private final List<TagEntity> tags;

    public DishTestBuilder() {
        dish = new DishEntity();
        tags = new ArrayList<>();
    }

    public DishTestBuilder withDishId(Long dishId) {
        dish.setId(dishId);
        return this;
    }

    public DishTestBuilder withUserId(Long userId) {
        dish.setUserId(userId);
        return this;
    }

    DishTestBuilder withTag(Long tagId) {
        return withTag(tagId, TagType.Ingredient);
    }


    public DishTestBuilder withTag(Long tagId, TagType tagType) {
        TagEntity tag = new TagEntity(tagId);
        tag.setTagType(tagType);

        return withTag(tag);

    }

    public DishTestBuilder withTag(TagEntity tag) {

        tags.add(tag);
        return this;
    }

    public DishTestBuilder withName(String dishName) {
        dish.setDishName(dishName);
        return this;
    }

    public DishEntity build() {
        dish.setTags(tags);
        return dish;
    }

    public Dish buildModel() {
        dish.setTags(tags);
        return ModelMapper.toModel(dish, true);
    }


}