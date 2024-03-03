package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;

import java.util.List;

public interface ConversionFactorSource {

    List<ConversionFactor> getFactors(Long unitId, Long tagId);

    ConversionFactor getFactor(Long fromUnitId, Long toUnitId);
}
