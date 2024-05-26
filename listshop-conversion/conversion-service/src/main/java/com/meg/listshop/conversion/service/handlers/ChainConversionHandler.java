package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public interface ChainConversionHandler extends ConversionHandler {


    boolean handles(ConversionSpec fromUnit, ConversionSpec toUnit);

    boolean handlesDomain(UnitType sourceDomain, UnitType targetDomain);
    boolean convertsToDomain(UnitType targetDomain);

    ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionContext context) throws ConversionFactorException;

    ConversionSpec getSource();
    ConversionSpec getTarget();

    ConversionSpec getOppositeSource(UnitType unitType);
}
