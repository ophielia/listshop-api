/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedDish {

    @JsonProperty("dish_id")
    private String dishId;

    private String name;

    @JsonProperty("last_used")
    private Date lastUsed;

    NestedDish() {
    }

    public NestedDish(Long dishId, String name, Date lastUsed) {
        this.dishId = String.valueOf(dishId);
        this.name = name;
        this.lastUsed = lastUsed;
    }

    public String getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public NestedDish withDishId(String dishId) {
        this.dishId = dishId;
        return this;
    }

    public NestedDish withName(String name) {
        this.name = name;
        return this;
    }


    @Override
    public String toString() {
        return "Dish{" +
                "dishId='" + dishId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
