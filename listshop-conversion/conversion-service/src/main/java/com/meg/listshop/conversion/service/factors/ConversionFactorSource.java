package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.List;

public interface ConversionFactorSource {

    List<ConversionFactor> getFactors(ConvertibleAmount convertibleAmount, Long conversionId);

    ConversionFactor getFactor(Long fromUnitId, Long toUnitId);
}
