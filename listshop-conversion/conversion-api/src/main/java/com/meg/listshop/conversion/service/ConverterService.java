package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.AddScaleRequest;
import com.meg.listshop.conversion.data.pojo.ConversionRequest;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;

public interface ConverterService {


    ConvertibleAmount convert(ConvertibleAmount amount, DomainType domain) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, ConversionRequest request) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity unit) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount add(ConvertibleAmount amountToAdd, ConvertibleAmount addTo, AddScaleRequest addRequest) throws ConversionPathException, ConversionFactorException, ConversionAddException;

    ConvertibleAmount scale(ConvertibleAmount summary, AddScaleRequest addRequest) throws ConversionFactorException;
}
