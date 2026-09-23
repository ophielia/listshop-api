/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FoodUnit {

@JsonProperty("unit_id")
    private String unitId;
    private String name;

    public FoodUnit() {
    }


    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FoodUnit{" +
                "unitId='" + unitId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
