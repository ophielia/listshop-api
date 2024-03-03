package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

public class TestScalingHandler extends AbstractScalingHandler {


    public TestScalingHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource, ConversionContextType scalingType) {
        super(source, target, conversionSource, scalingType);
    }
}
