package com.meg.listshop.conversion.data.pojo;

public class ConversionContext {
    private final ConversionContextType contextType;

    private final UnitType unitType;

    public ConversionContext(ConversionContextType contextType, UnitType unitType) {
        this.contextType = contextType;
        this.unitType = unitType;
    }

    public ConversionContextType getContextType() {
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
