/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PutDish {

    @JsonProperty("dish_id")
    private String dishId;

    @JsonProperty("name")
    private String dishName;

    private String description;

    private String reference;

    public PutDish(String dishId, String dishName, String description, String reference) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.description = description;
        this.reference = reference;
    }

    public PutDish() {
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
