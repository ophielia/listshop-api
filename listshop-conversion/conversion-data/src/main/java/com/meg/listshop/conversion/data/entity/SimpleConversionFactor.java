package com.meg.listshop.conversion.data.entity;

public class SimpleConversionFactor implements ConversionFactor {
    private Double factor;

    private UnitEntity toUnit;

    private UnitEntity fromUnit;
    private String marker;

    public static ConversionFactor reverseFactor(ConversionFactor factor) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(1 / factor.getFactor());
        reversed.setToUnit(factor.getFromUnit());
        reversed.setFromUnit(factor.getToUnit());
        reversed.setMarker(factor.getMarker());
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
}
