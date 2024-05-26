package com.meg.listshop.conversion.data.entity;

import com.meg.listshop.common.data.entity.UnitEntity;

public interface ConversionFactor {
    UnitEntity getFromUnit();

    Double getFactor();

    UnitEntity getToUnit();
    String getMarker();
    String getUnitSize();
    Boolean isUnitDefault();
}
