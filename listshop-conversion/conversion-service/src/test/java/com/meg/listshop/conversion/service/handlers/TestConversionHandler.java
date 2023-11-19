package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.repository.ConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;

public class TestConversionHandler extends AbstractConversionHandler {


    public TestConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source, target, conversionSource);
    }
}
