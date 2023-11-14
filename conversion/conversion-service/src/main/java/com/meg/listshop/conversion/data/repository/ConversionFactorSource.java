package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.List;

public interface ConversionFactorSource {
    List<ConversionFactor> getFactors(UnitType fromType);

    ConversionFactor getFactor(Long fromUnitId, Long toUnitId);
}
