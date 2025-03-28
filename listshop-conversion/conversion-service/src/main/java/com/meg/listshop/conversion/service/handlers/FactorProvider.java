package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.List;

public interface FactorProvider {

    List<ConversionFactor> provideFactors(Long conversionId);

}
