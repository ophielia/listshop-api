package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class CategoryResource extends AbstractListShopResource implements ListShopModel {

    private Category category;

    public CategoryResource(Category category) {
        this.category = category;
    }

    public CategoryResource() {
        // an empty constructor
    }

    public Category getCategory() {
        return category;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "category";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return category.getId().toString();
    }
}