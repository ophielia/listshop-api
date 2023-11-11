package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public interface ConversionHandler {


    boolean convertsTo(UnitType toUnit);

    boolean handles(UnitType fromUnit, UnitType toUnit);

    ConvertibleAmount convert(ConvertibleAmount toConvert) throws ConversionFactorException;
}
