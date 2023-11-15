package com.meg.listshop.conversion.data.entity;

public class SimpleConversionFactor implements ConversionFactor {
    private Double factor;

    private Unit toUnit;

    private Unit fromUnit;

    public static ConversionFactor reverseFactor(ConversionFactor factor) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(1 / factor.getFactor());
        reversed.setToUnit(factor.getFromUnit());
        reversed.setFromUnit(factor.getToUnit());
        return reversed;
    }

    public static ConversionFactor conversionFactor(Unit fromUnit, Unit toUnit, double factor) {
        SimpleConversionFactor reversed = new SimpleConversionFactor();
        reversed.setFactor(factor);
        reversed.setToUnit(toUnit);
        reversed.setFromUnit(fromUnit);
        return reversed;
    }

    public static ConversionFactor passThroughFactor(ConversionFactor factor) {
        SimpleConversionFactor passThrough = new SimpleConversionFactor();
        passThrough.setFactor(1D);
        passThrough.setToUnit(factor.getToUnit());
        passThrough.setFromUnit(factor.getToUnit());
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
    public Unit getToUnit() {
        return toUnit;
    }

    public void setToUnit(Unit toUnit) {
        this.toUnit = toUnit;
    }

    @Override
    public Unit getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(Unit fromUnit) {
        this.fromUnit = fromUnit;
    }
}
