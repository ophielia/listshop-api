package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.MeasurementDomain;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

public interface ConversionService {

    ConvertibleAmount convert(ConvertibleAmount amount, MeasurementDomain domain);

    ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context);

    ConvertibleAmount convert(ConvertibleAmount amount, Unit unit) throws ConversionPathException, ConversionFactorException;
}
