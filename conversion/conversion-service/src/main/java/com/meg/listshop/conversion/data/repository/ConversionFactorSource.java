package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;

import java.util.List;

public interface ConversionFactorSource {

    List<ConversionFactor> getFactors(Long unitId);

    ConversionFactor getFactor(Long fromUnitId, Long toUnitId);
}
