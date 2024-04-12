package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;

import java.util.List;

public interface ConversionService {


    List<ConversionSampleDTO> conversionSamplesForId(Long tagId, Boolean isLiquid);

    void saveConversionFactors(Long conversionId, List<FoodFactor> foodFactors);
}
