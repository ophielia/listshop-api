package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;

public interface ConvertibleAmount {

    double getQuantity();

    UnitEntity getUnit();

    Long getTagId();

    String getTagName();

    Boolean getIsLiquid();
}
