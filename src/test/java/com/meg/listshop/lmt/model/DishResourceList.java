package com.meg.listshop.lmt.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class DishResourceList extends ResourceSupport {

    @JsonProperty("dishResourceList")
    private ResultDishResource[] dishList = {};

    public ResultDishResource[] getDishList() {
        return dishList;
    }

    public void setDishList(ResultDishResource[] dishList) {
        this.dishList = dishList;
    }
}