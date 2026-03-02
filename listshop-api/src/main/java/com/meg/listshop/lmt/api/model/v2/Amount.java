package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Amount {

    @JsonProperty("whole_quantity")
    private Integer wholeQuantity;
    @JsonProperty("fractional_quantity")
    private String fractionalQuantity;
    @JsonProperty("quantity_display")
    private String quantityDisplay;
    @JsonProperty("unit_id")
    private String unitId;
    @JsonProperty("unit_display")
    private String unitDisplay;

    public Amount() {
        // empty constructor
    }

    public Amount withWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
        return this;
    }

    public Amount withFractionalQuantity(String fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
        return this;
    }

    public Amount withQuantityDisplay(String quantityDisplay) {
        this.quantityDisplay = quantityDisplay;
        return this;
    }

    public Amount withUnitId(String unitId) {
        this.unitId = unitId;
        return this;
    }

    public Amount withUnitDisplay(String unitDisplay) {
        this.unitDisplay = unitDisplay;
        return this;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public String getFractionalQuantity() {
        return fractionalQuantity;
    }

    public String getQuantityDisplay() {
        return quantityDisplay;
    }

    public String getUnitId() {
        return unitId;
    }

    public String getUnitDisplay() {
        return unitDisplay;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity='" + fractionalQuantity + '\'' +
                ", quantityDisplay='" + quantityDisplay + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitDisplay='" + unitDisplay + '\'' +
                '}';
    }
}
