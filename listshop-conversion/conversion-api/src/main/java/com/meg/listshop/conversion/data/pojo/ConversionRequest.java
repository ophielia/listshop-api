package com.meg.listshop.conversion.data.pojo;

public class ConversionRequest {
    private final ConversionTargetType contextType;

    private final UnitType unitType;

    public ConversionRequest(ConversionTargetType contextType, UnitType unitType) {
        this.contextType = contextType;
        this.unitType = unitType;
    }

    public ConversionTargetType getContextType() {
        return contextType;
    }


    public UnitType getUnitType() {
        return unitType;
    }

    @Override
    public String toString() {
        return "ConversionContext{" +
                "contextType=" + contextType +
                ", unitType=" + unitType +
                '}';
    }
}
