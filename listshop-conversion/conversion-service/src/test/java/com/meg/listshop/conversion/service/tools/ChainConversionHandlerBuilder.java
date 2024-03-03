package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.ScalingHandler;
import com.meg.listshop.conversion.service.handlers.TestChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestScalingHandler;

import java.util.List;

public  class ChainConversionHandlerBuilder extends ConversionHandlerBuilder<ChainConversionHandler> {

    public ChainConversionHandlerBuilder() {
    }

    @Override
    public ChainConversionHandler internalBuild(ConversionSpec fromSpec, ConversionSpec toSpec, List<ConversionFactor> factorList) {
        ConversionFactorSource source = new TestConversionFactorSource(factorList, false);
        return new TestChainConversionHandler(fromSpec, toSpec, source);
    }



}
