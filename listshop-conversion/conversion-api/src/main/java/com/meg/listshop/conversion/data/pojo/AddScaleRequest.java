package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public class AddScaleRequest {
    ConversionTargetType contextType;


    private String unitSize;
    private UnitType type;
    private UnitSubtype subtype;

    public AddScaleRequest(ConversionTargetType contextType, ConvertibleAmount amount) {
        UnitEntity unit = amount.getUnit();
        this.contextType = contextType;
        this.type = unit.getType();
        this.subtype = unit.getSubtype();
        this.unitSize = amount.getUnitSize();
    }

    public AddScaleRequest(ConversionTargetType contextType, UnitEntity unit, String unitSize) {
        this.contextType = contextType;
        this.type = unit.getType();
        this.subtype = unit.getSubtype();
        this.unitSize = unitSize;
    }

    public ConversionTargetType getContextType() {
        return contextType;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public UnitType getUnitType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public UnitSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(UnitSubtype subtype) {
        this.subtype = subtype;
    }

    @Override
    public String toString() {
        return "AddRequest{" +
                "contextType=" + contextType +
                ", unitSize='" + unitSize + '\'' +
                ", type=" + type +
                ", subtype=" + subtype +
                '}';
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }
}
