package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.Objects;

public class SimpleAmount  implements ConvertibleAmount {

    private double quantity;
    private UnitEntity unit;

    private Long conversionId = null;

    private Boolean isLiquid;

    private String marker;
    private String unitSize;
    private Boolean userSize = false;


    public SimpleAmount(double quantity, UnitEntity unit, Long conversionId, Boolean isLiquid, String marker) {
        this.quantity = quantity;
        this.unit = unit;
        this.conversionId = conversionId;
        this.isLiquid = isLiquid;
        this.marker = marker;
    }

    public SimpleAmount(double quantity, UnitEntity unit, Long conversionId, Boolean isLiquid, String marker, String unitSize, Boolean userSize) {
        this.quantity = quantity;
        this.unit = unit;
        this.conversionId = conversionId;
        this.isLiquid = isLiquid;
        this.marker = marker;
        this.unitSize = unitSize;
        this.userSize = userSize;
    }

    public SimpleAmount(double quantity, UnitEntity unit) {
        this(quantity, unit, (String) null);
    }

    public SimpleAmount(double quantity, UnitEntity unit, String unitSize) {
        this.quantity = quantity;
        this.unit = unit;
        this.unitSize = unitSize;
    }

    public SimpleAmount(double newQuantity, UnitEntity newUnit, ConvertibleAmount toConvert) {
       this(newQuantity, newUnit,toConvert, (String) null);
    }

    public SimpleAmount(double newQuantity, UnitEntity newUnit, ConvertibleAmount toConvert, String unitSize) {
        this.quantity = newQuantity;
        this.unit = newUnit;
        this.conversionId = toConvert.getConversionId();
        this.isLiquid = toConvert.getIsLiquid();
        this.unitSize = unitSize;
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
        return conversionId;
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
    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public double getQuantityRoundedUp() {
        return RoundingUtils.roundUpToNearestFraction(quantity);
    }

    @Override
    public Boolean getUserSize() {
        return userSize != null ? userSize : false;
    }

    public void setUserSize(Boolean userSize) {
        this.userSize = userSize;
    }

    @Override
    public String toString() {
        return "SimpleAmount{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", tagId=" + conversionId +
                ", isLiquid=" + isLiquid +
                ", marker=" + marker +
                ", unitSize=" + unitSize +
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
