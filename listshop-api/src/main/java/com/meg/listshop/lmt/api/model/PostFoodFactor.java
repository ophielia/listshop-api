/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostFoodFactor {

    @JsonProperty("from_unit_id")
    private String fromUnitId;
    @JsonProperty("to_unit_id")
    private String toUnitId;
    @JsonProperty("from_quantity")
    private String fromQuantity;
    @JsonProperty("to_quantity")
    private String toQuantity;

    public PostFoodFactor() {
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(String fromUnitId) {
        this.fromUnitId = fromUnitId;
    }

    public String getToUnitId() {
        return toUnitId;
    }

    public void setToUnitId(String toUnitId) {
        this.toUnitId = toUnitId;
    }

    public String getFromQuantity() {
        return fromQuantity;
    }

    public void setFromQuantity(String fromQuantity) {
        this.fromQuantity = fromQuantity;
    }

    public String getToQuantity() {
        return toQuantity;
    }

    public void setToQuantity(String toQuantity) {
        this.toQuantity = toQuantity;
    }

    @Override
    public String toString() {
        return "FoodFactor{" +
                "fromUnitId='" + fromUnitId + '\'' +
                ", toUnitId='" + toUnitId + '\'' +
                ", fromQuantity='" + fromQuantity + '\'' +
                ", toQuantity='" + toQuantity + '\'' +
                '}';
    }
}
