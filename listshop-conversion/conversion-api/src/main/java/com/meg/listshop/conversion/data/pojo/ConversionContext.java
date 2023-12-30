package com.meg.listshop.conversion.data.pojo;

public class ConversionContext {
    private ConversionContextType contextType;

    private UnitType unitType;
    private UnitSubtype unitSubtype;

    public ConversionContext(ConversionContextType contextType, UnitType unitType, UnitSubtype subtype) {
        this.contextType = contextType;
        this.unitType = unitType;
        this.unitSubtype = subtype;
    }

    public ConversionContext(ConversionContextType contextType, UnitType unitType) {
        this(contextType, unitType, null);
    }

    public ConversionContextType getContextType() {
        return contextType;
    }


    public UnitType getUnitType() {
        return unitType;
    }

    public UnitSubtype getUnitSubtype() {
        return unitSubtype;
    }

    @Override
    public String toString() {
        return "ConversionContext{" +
                "contextType=" + contextType +
                ", unitType=" + unitType +
                ", unitSubtype=" + unitSubtype +
                '}';
    }
}
