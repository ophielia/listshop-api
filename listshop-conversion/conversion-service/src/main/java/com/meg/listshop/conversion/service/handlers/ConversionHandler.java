package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public interface ConversionHandler {

    ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionContext context) throws ConversionFactorException;

}
