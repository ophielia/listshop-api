package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LayoutCategoryResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("layout_category")
    private LayoutCategory layoutCategory;

    public LayoutCategoryResource(LayoutCategory foodCategoryMapping) {
        this.layoutCategory = foodCategoryMapping;
    }

    public LayoutCategoryResource() {
    }

    public LayoutCategoryResource(Category category) {
    }

    public LayoutCategory getLayoutCategory() {
        return layoutCategory;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "category";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return "none";
    }
}
