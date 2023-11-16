package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.Objects;

public class SimpleAmount implements ConvertibleAmount {

    private double quantity;
    private UnitEntity unit;

    private Long tagId = null;

    private Boolean isLiquid;

    private String tagName;


    public SimpleAmount(double quantity, UnitEntity unit, Long tagId, Boolean isLiquid, String tagName) {
        this.quantity = quantity;
        this.unit = unit;
        this.tagId = tagId;
        this.isLiquid = isLiquid;
        this.tagName = tagName;
    }

    public SimpleAmount(double quantity, UnitEntity unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public SimpleAmount(double newQuantity, UnitEntity newUnit, ConvertibleAmount toConvert) {
        this.quantity = newQuantity;
        this.unit = newUnit;
        this.tagId = toConvert.getTagId();
        this.isLiquid = toConvert.getIsLiquid();
        this.tagName = toConvert.getTagName();
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    @Override
    public UnitEntity getUnit() {
        return unit;
    }

    @Override
    public Long getTagId() {
        return tagId;
    }

    @Override
    public Boolean getIsLiquid() {
        return isLiquid;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        return "SimpleAmount{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", tagId=" + tagId +
                ", isLiquid=" + isLiquid +
                ", tagName=" + tagName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAmount that = (SimpleAmount) o;
        return Double.compare(quantity, that.quantity) == 0 && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, unit);
    }
}
