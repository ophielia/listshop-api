package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestConversionHandler;

import java.util.List;

public  class StandardConversionHandlerBuilder extends ConversionHandlerBuilder<ConversionHandler> {

    public StandardConversionHandlerBuilder() {
    }

    @Override
    public ConversionHandler internalBuild(ConversionSpec fromSpec, ConversionSpec toSpec, List<ConversionFactor> factorList) {
        ConversionFactorSource source = new TestConversionFactorSource(factorList, true);
        return new TestConversionHandler(fromSpec, toSpec, source);
    }



}
