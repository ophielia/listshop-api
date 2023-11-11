package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.Objects;

public class HandlerChainKey {

    private UnitType fromUnitType;
    private UnitType toUnitType;

    private ConversionContextType context;

    public HandlerChainKey(UnitType fromUnitType, UnitType toUnitType, ConversionContextType context) {
        this.fromUnitType = fromUnitType;
        this.toUnitType = toUnitType;
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerChainKey that = (HandlerChainKey) o;
        return fromUnitType == that.fromUnitType && toUnitType == that.toUnitType && context == that.context;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUnitType, toUnitType, context);
    }

    @Override
    public String toString() {
        return "HandlerChainKey{" +
                "fromUnitId=" + fromUnitType +
                ", toUnitId=" + toUnitType +
                ", context=" + context +
                '}';
    }
}
