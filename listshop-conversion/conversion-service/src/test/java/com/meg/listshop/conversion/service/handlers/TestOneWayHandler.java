package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.repository.ConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;

public class TestOneWayHandler extends AbstractOneWayConversionHandler {


    public TestOneWayHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source, target, conversionSource);
    }
}
