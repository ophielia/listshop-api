package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class FoodCategoryMappingResource extends AbstractListShopResource implements ListShopModel {

    private FoodCategoryMapping foodCategoryMapping;

    public FoodCategoryMappingResource(FoodCategoryMapping foodCategoryMapping) {
        this.foodCategoryMapping = foodCategoryMapping;
    }

    public FoodCategoryMappingResource() {
    }

    public FoodCategoryMapping getFoodCategoryMapping() {
        return foodCategoryMapping;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "category_mapping";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return "none";
    }
}