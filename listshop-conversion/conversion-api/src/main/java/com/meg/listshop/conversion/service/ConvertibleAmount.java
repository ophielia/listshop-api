package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;

public interface ConvertibleAmount {

    double getQuantity();

    UnitEntity getUnit();

    Long getConversionId();

    String getMarker();

    Boolean getIsLiquid();

    String getUnitSize();

    Boolean getUserSize();

    double getQuantityRoundedUp();

    default SimpleAmount copy() {
        return new SimpleAmount(
                getQuantity(),
                getUnit(),
                getConversionId().longValue(),
                getIsLiquid().booleanValue(),
                getMarker()
        );

    }
}
