package com.meg.listshop.lmt.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;

public class ResultDishResource {

    private ResultDish dish;

    @JsonProperty("_links")
    @JsonIgnore
    private Object links;


    public ResultDish getDish() {
        return dish;
    }
}