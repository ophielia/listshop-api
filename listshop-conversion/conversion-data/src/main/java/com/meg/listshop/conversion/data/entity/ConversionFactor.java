package com.meg.listshop.conversion.data.entity;

public interface ConversionFactor {
    UnitEntity getFromUnit();

    Double getFactor();

    UnitEntity getToUnit();
    String getMarker();
}
