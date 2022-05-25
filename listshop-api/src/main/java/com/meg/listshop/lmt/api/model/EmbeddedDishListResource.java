package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedDishListResource {

    @JsonProperty("dishResourceList")
    private List<DishResource> dishResourceList;

    public EmbeddedDishListResource(List<DishResource> listLayoutResourceList) {
        this.dishResourceList = listLayoutResourceList;
    }

    public EmbeddedDishListResource() {
        // this is empty because I want it to be empty
    }

    public List<DishResource> getDishResourceList() {
        return dishResourceList;
    }

    public void setDishResourceList(List<DishResource> listLayoutResourceList) {
        this.dishResourceList = listLayoutResourceList;
    }
}