package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

import java.util.List;

public interface ConversionService {


    List<ConversionSampleDTO> conversionSamplesForId(Long tagId, Boolean isLiquid);

    void saveConversionFactors(Long conversionId, List<FoodFactor> foodFactors);

    ConvertibleAmount convertToUnit(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException;
}
