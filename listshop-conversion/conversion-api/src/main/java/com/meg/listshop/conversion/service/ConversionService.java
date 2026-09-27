/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

import java.util.List;

public interface ConversionService {

    void saveConversionFactors(Long conversionId, List<FoodFactor> foodFactors);

    void addManualConversionFactor(Long conversionId, SimpleConversionFactor conversionFactor);

    List<ConversionFactorEntity> manualConversionFactorsForConversionId(Long conversionId);

    ConvertibleAmount convertToUnit(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException;

    String getDefaultUnitSizeForConversionId(Long conversionId, Long unitId);

    void removeManualConversionFactors(Long conversionId);
}
