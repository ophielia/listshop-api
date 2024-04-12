package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

public class TestScalingHandler extends AbstractScalingHandler {


    public TestScalingHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource, ConversionTargetType scalingType) {
        super(source, target, conversionSource, scalingType);
    }
}
