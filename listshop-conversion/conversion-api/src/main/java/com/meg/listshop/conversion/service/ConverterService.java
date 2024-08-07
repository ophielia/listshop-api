package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionRequest;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

public interface ConverterService {


    ConvertibleAmount convert(ConvertibleAmount amount, DomainType domain) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, ConversionRequest context) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, ConversionRequest context, String unitSize) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity unit) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException;
}
