package com.meg.listshop.conversion.data.entity;

public interface ConversionFactor {
    Unit getFromUnit();

    Double getFactor();

    Unit getToUnit();
}
