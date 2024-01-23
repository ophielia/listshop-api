package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.Objects;

public class NewHandlerChainKey {

    private UnitType target;
    private UnitType source;

    public NewHandlerChainKey(ConversionSpec source, ConversionSpec target) {
        this.source = source.getUnitType();
        this.target = target.getUnitType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewHandlerChainKey that = (NewHandlerChainKey) o;
        return Objects.equals(target, that.target) && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, source);
    }

    @Override
    public String toString() {
        return "HandlerChainKey{" +
                ", target=" + target +
                ", source=" + source +
                '}';
    }
}
