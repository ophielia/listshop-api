package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodCategoryResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("food_category")
    private FoodCategory foodCategory;

    public FoodCategoryResource(FoodCategory foodCategoryMapping) {
        this.foodCategory = foodCategoryMapping;
    }

    public FoodCategoryResource() {
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "categor";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return "none";
    }
}