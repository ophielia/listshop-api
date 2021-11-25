package com.meg.listshop.lmt.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

public class DishResourceList extends RepresentationModel {

    @JsonProperty("dishResourceList")
    private ResultDishResource[] dishList = {};

    public ResultDishResource[] getDishList() {
        return dishList;
    }

    public void setDishList(ResultDishResource[] dishList) {
        this.dishList = dishList;
    }
}