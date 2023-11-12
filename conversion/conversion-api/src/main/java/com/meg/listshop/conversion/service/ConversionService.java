package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

public interface ConversionService {


    ConvertibleAmount convert(ConvertibleAmount amount, UnitType domain) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, Unit unit) throws ConversionPathException, ConversionFactorException;
}
