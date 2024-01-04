package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.List;

public interface ConversionHandler {


    boolean convertsTo(ConversionSpec spec);

    boolean handles(ConversionSpec fromUnit, ConversionSpec toUnit);

    ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionSpec targetSpec) throws ConversionFactorException, ExceedsAllowedScaleException;

    ConversionSpec getSource();
    List<ConversionSpec> getAllSources();

    ConversionSpec getTarget();

}
