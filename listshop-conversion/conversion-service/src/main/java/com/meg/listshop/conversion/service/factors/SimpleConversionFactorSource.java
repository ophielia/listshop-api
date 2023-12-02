package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;

import java.util.List;

public class SimpleConversionFactorSource extends AbstractConversionFactorSource {


    public SimpleConversionFactorSource(List<ConversionFactor> factors) {
        super(factors);
    }

    public SimpleConversionFactorSource(List<ConversionFactor> factors, boolean oneWay) {
        super(factors, oneWay);
    }
}
