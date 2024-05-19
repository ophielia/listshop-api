package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IngredientPut {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tag_id")
    private String tagId;
    @JsonProperty("tag_display")
    private String tagDisplay;
    @JsonProperty("whole_quantity")
    private Integer wholeQuantity;
    @JsonProperty("fractional_quantity")
    private String fractionalQuantity;
    @JsonProperty("quantity_display")
    private String quantityDisplay;
    @JsonProperty("unit_id")
    private String unitId;
    @JsonProperty("unit_name")
    private String unitName;
    @JsonProperty("raw_modifiers")
    private String rawModifiers;
    @JsonProperty("unit_display")
    private String unitDisplay;

    public IngredientPut() {
        // empty constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagDisplay() {
        return tagDisplay;
    }

    public void setTagDisplay(String tagDisplay) {
        this.tagDisplay = tagDisplay;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
    }

    public String getFractionalQuantity() {
        return fractionalQuantity;
    }

    public void setFractionalQuantity(String fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
    }

    public String getQuantityDisplay() {
        return quantityDisplay;
    }

    public void setQuantityDisplay(String quantityDisplay) {
        this.quantityDisplay = quantityDisplay;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getRawModifiers() {
        return rawModifiers;
    }

    public void setRawModifiers(String rawModifiers) {
        this.rawModifiers = rawModifiers;
    }

    public String getUnitDisplay() {
        return unitDisplay;
    }

    public void setUnitDisplay(String unitDisplay) {
        this.unitDisplay = unitDisplay;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                ", tagId='" + tagId + '\'' +
                ", tagDisplay='" + tagDisplay + '\'' +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity='" + fractionalQuantity + '\'' +
                ", quantityDisplay='" + quantityDisplay + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitName='" + unitName + '\'' +
                ", rawModifiers='" + rawModifiers + '\'' +
                ", unitDisplay='" + unitDisplay + '\'' +
                '}';
    }
}