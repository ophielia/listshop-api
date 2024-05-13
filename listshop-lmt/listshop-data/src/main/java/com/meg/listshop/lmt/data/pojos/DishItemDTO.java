package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.FractionType;

public class DishItemDTO {

    private Long dishItemId;
    private Long tagId;
    private Long unitId;
    private Double quantity;
    private Integer wholeQuantity;
    private FractionType fractionalQuantity;
    private String rawModifiers;
    private String unitName;
    private String marker; //MM do we need this?
    private String unitSize; //MM same - do we need this?

    public DishItemDTO(Long dishItemId, Long tagId, Long unitId) {
        this.dishItemId = dishItemId;
        this.tagId = tagId;
        this.unitId = unitId;
    }

    public DishItemDTO() {
    }

    public Long getDishItemId() {
        return dishItemId;
    }

    public void setDishItemId(Long dishItemId) {
        this.dishItemId = dishItemId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
    }

    public FractionType getFractionalQuantity() {
        return fractionalQuantity;
    }

    public void setFractionalQuantity(FractionType fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
    }

    public String getRawModifiers() {
        return rawModifiers;
    }

    public void setRawModifiers(String rawModifiers) {
        this.rawModifiers = rawModifiers;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    @Override
    public String toString() {
        return "DishItemDTO{" +
                "dishItemId=" + dishItemId +
                ", tagId=" + tagId +
                ", unitId=" + unitId +
                ", quantity=" + quantity +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", rawModifiers='" + rawModifiers + '\'' +
                ", unitName='" + unitName + '\'' +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                '}';
    }
}