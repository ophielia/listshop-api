package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

public class TestOneWayHandler extends AbstractOneWayConversionHandler {


    public TestOneWayHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source, target, conversionSource);
    }
}
