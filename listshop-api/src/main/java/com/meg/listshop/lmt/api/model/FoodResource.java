package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class FoodResource extends AbstractListShopResource implements ListShopModel {

    private Food food;

    public FoodResource(Food food) {
        this.food = food;
    }

    public FoodResource() {
    }

    public Food getFood() {
        return food;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "food";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return food.getId();
    }
}