/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

    @JsonProperty("whole_quantity")
    private Integer wholeQuantity;
    @JsonProperty("fractional_quantity")
    private String fractionalQuantity;
    @JsonProperty
    private Double quantity;
    @JsonProperty("rounded_quantity")
    private Double roundedQuantity;
    @JsonProperty("quantity_display")
    private String quantityDisplay;
    @JsonProperty("unit_id")
    private String unitId;
    @JsonProperty("unit_display")
    private String unitDisplay;
    @JsonProperty("display")
    private String display;
    @JsonProperty("modifiers")
    private List<String> modifiers;

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

    public Amount withQuantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }


    public Amount withRoundedQuantity(Double quantity) {
        this.roundedQuantity = quantity;
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


    public Amount withDisplay(String displayValue) {
        this.display = displayValue;
        return this;
    }

    public Amount withModifiers(List<String> rawModifiers) {
        this.modifiers = rawModifiers;
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

    public String getDisplay() {
        return display;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public double getQuantity() {
    return quantity;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity='" + fractionalQuantity + '\'' +
                ", quantityDisplay='" + quantityDisplay + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitDisplay='" + unitDisplay + '\'' +
                ", rawEntry='" + display + '\'' +
                ", rawModifiers='" + modifiers + '\'' +
                '}';
    }

}
