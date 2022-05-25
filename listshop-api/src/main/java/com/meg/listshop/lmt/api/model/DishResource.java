package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class DishResource extends AbstractListShopResource implements ListShopModel {

    private Dish dish;

    public DishResource(Dish dish) {
        this.dish = dish;
    }

    public Dish getDish() {
        return dish;
    }

    public DishResource() {
        // an empty constructor
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "dish";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return dish.getId().toString();
    }
}