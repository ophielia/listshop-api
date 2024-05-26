package com.meg.listshop.conversion.data.entity;

import com.meg.listshop.common.data.entity.UnitEntity;

public class SimpleConversionFactor implements ConversionFactor {
    private Double factor;

    private UnitEntity toUnit;

    private UnitEntity fromUnit;
    private String marker;
    private String unitSize;
    private Boolean unitDefault;

    public static ConversionFactor reverseFactor(ConversionFactor factor) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(1 / factor.getFactor());
        reversed.setToUnit(factor.getFromUnit());
        reversed.setFromUnit(factor.getToUnit());
        reversed.setMarker(factor.getMarker());
        reversed.setUnitSize(factor.getUnitSize());
        reversed.setUnitDefault(factor.isUnitDefault());
        return reversed;
    }

    public static ConversionFactor conversionFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(factor);
        reversed.setToUnit(toUnit);
        reversed.setFromUnit(fromUnit);
        return reversed;
    }

    public static ConversionFactor conversionFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor, String marker) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(factor);
        reversed.setToUnit(toUnit);
        reversed.setFromUnit(fromUnit);
        reversed.setMarker(marker);
        return reversed;
    }

    public static ConversionFactor conversionFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor, String marker, String unitSize) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(factor);
        reversed.setToUnit(toUnit);
        reversed.setFromUnit(fromUnit);
        reversed.setMarker(marker);
        reversed.setUnitSize(unitSize);
        return reversed;
    }

    public static ConversionFactor passThroughFactor(ConversionFactor factor) {
        SimpleConversionFactor passThrough = new SimpleConversionFactor();
        passThrough.setFactor(1D);
        passThrough.setToUnit(factor.getToUnit());
        passThrough.setFromUnit(factor.getToUnit());
        return passThrough;
    }

    public static ConversionFactor passThroughFactor(UnitEntity unit) {
        SimpleConversionFactor passThrough = new SimpleConversionFactor();
        passThrough.setFactor(1D);
        passThrough.setToUnit(unit);
        passThrough.setFromUnit(unit);
        return passThrough;
    }

    @Override
    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    @Override
    public UnitEntity getToUnit() {
        return toUnit;
    }

    public void setToUnit(UnitEntity toUnit) {
        this.toUnit = toUnit;
    }

    @Override
    public UnitEntity getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(UnitEntity fromUnit) {
        this.fromUnit = fromUnit;
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

    public Boolean isUnitDefault() {
        return unitDefault != null && unitDefault;
    }

    public void setUnitDefault(Boolean unitDefault) {
        this.unitDefault = unitDefault;
    }
}
