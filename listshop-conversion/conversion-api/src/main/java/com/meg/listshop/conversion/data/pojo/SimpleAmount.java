package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.Objects;

public class SimpleAmount implements ConvertibleAmount {

    private double quantity;
    private UnitEntity unit;

    private Long tagId = null;

    private Boolean isLiquid;

    private String marker;


    public SimpleAmount(double quantity, UnitEntity unit, Long tagId, Boolean isLiquid, String marker) {
        this.quantity = quantity;
        this.unit = unit;
        this.tagId = tagId;
        this.isLiquid = isLiquid;
        this.marker = this.marker;
    }
    public SimpleAmount(double quantity, UnitEntity unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public SimpleAmount(double newQuantity, UnitEntity newUnit, ConvertibleAmount toConvert) {
        this.quantity = newQuantity;
        this.unit = newUnit;
        this.tagId = toConvert.getConversionId();
        this.isLiquid = toConvert.getIsLiquid();
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
    public Long getConversionId() {
        return tagId;
    }

    @Override
    public Boolean getIsLiquid() {
        return isLiquid;
    }

    @Override
    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    @Override
    public String toString() {
        return "SimpleAmount{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", tagId=" + tagId +
                ", isLiquid=" + isLiquid +
                ", marker=" + marker +
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
