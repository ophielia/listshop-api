package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;

public interface ConvertibleAmount {

    double getQuantity();

    UnitEntity getUnit();

    Long getConversionId();

    String getMarker();

    Boolean getIsLiquid();

    String getUnitSize();

    double getQuantityRoundedUp();
}
