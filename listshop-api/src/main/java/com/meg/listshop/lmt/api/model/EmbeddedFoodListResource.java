package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedFoodListResource {

    @JsonProperty("food_resource_list")
    private List<FoodResource> foodResourceList;

    public EmbeddedFoodListResource(List<FoodResource> foodResourceList) {
        this.foodResourceList = foodResourceList;
    }

    public EmbeddedFoodListResource() {
    }

    public List<FoodResource> getFoodResourceList() {
        return foodResourceList;
    }

    public void setFoodResourceList(List<FoodResource> foodResourceList) {
        this.foodResourceList = foodResourceList;
    }
}