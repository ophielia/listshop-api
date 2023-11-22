package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.service.factors.AbstractConversionFactorSource;

import java.util.List;

public class TestConversionFactorSource extends AbstractConversionFactorSource {


    public TestConversionFactorSource(List<ConversionFactor> factors) {
        super(factors);
    }

    public TestConversionFactorSource(List<ConversionFactor> factors, boolean oneWay) {
        super(factors, oneWay);
    }


}
