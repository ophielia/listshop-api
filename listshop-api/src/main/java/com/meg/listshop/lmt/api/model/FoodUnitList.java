/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FoodUnitList {

    @JsonProperty("unit_list")
    private List<FoodUnit> unitList;

    public FoodUnitList(List<FoodUnit> foodUnitList) {
        this.unitList = foodUnitList;
    }

    public FoodUnitList() {
    }

    public List<FoodUnit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<FoodUnit> unitList) {
        this.unitList = unitList;
    }

    @Override
    public String toString() {
        return "FoodUnitList{" +
                "unitList=" + unitList +
                '}';
    }
}
