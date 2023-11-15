package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;

public interface ConvertibleAmount {

    double getQuantity();

    Unit getUnit();

    Long getTagId();

    String getTagName();

    Boolean getIsLiquid();
}
