package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;

public interface ConvertibleAmount {

    double getQuantity();

    Unit getUnit();

    long getTagId();

    String getTagName();

    Boolean getIsLiquid();
}
