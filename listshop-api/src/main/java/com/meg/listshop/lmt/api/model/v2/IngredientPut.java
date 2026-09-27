/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IngredientPut {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tag_id")
    private String tagId;
    @JsonProperty("tag_display")
    private String tagDisplay;
    @JsonProperty("raw_entry")
    private String rawEntry;

    @JsonProperty
    private Amount amount;
    /*
    @JsonProperty("whole_quantity")
    private Integer wholeQuantity;
    @JsonProperty("fractional_quantity")
    private String fractionalQuantity;
    @JsonProperty("quantity")
    private Double quantity;
    @JsonProperty("quantity_display")
    private String quantityDisplay;
    @JsonProperty("unit_id")
    private String unitId;
    @JsonProperty("unit_display")
    private String unitDisplay;
    @JsonProperty("raw_modifiers")
    private List<String> rawModifiers;
*/

    public IngredientPut() {
        // empty constructor
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public Amount getAmount() {
        return amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @JsonIgnore
    public Integer getWholeQuantity() {
        if (amount != null) {
            return amount.getWholeQuantity();
        }
        return null;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        if (amount == null) {
            amount = new Amount();
        }
        amount = this.amount.withWholeQuantity(wholeQuantity);
    }

    @JsonIgnore
    public String getFractionalQuantity() {
        if (amount != null) {
            amount.getFractionalQuantity();
        }
        return null;
    }

    public void setFractionalQuantity(String fractionalQuantity) {
        if (amount == null) {
            amount = new Amount();
        }
        amount = amount.withFractionalQuantity(fractionalQuantity);
    }

    @JsonIgnore
    public String getUnitId() {
        if (amount != null) {
            amount.getUnitId();
        }
        return null;
    }

    public void setUnitId(String unitId) {
        if (amount == null) {
            amount = new Amount();
        }
        amount = amount.withUnitId(unitId);
    }

    @JsonIgnore
    public List<String> getRawModifiers() {
        if (amount != null) {
            amount.getModifiers();
        }
        return null;
    }

    @JsonIgnore
    public String getRawEntry() {
        return rawEntry;
    }

    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    @JsonIgnore
    public Double getQuantity() {
        if (amount != null) {
            return amount.getQuantity();
        }
        return null;
    }

    public void setQuantity(Double quantity) {
        if (amount == null) {
            amount = new Amount();
        }
        amount = amount.withQuantity(quantity);
    }

    @Override
    public String toString() {
        return "IngredientPut{" +
                "id='" + id + '\'' +
                ", tagId='" + tagId + '\'' +
                ", tagDisplay='" + tagDisplay + '\'' +
                ", rawEntry='" + rawEntry + '\'' +
                ", amount=" + amount +
                '}';
    }
}
