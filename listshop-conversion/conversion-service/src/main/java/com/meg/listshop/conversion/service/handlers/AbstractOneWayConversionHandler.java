package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

public abstract class AbstractOneWayConversionHandler extends AbstractConversionHandler {


    public AbstractOneWayConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source, target, conversionSource);
    }

    public boolean convertsTo(ConversionSpec spec) {
        return getTarget().equals(spec);
    }

    public boolean handles(ConversionSpec sourceSpec, ConversionSpec targetSpec) {
        return (getSource().equals(sourceSpec) && getTarget().equals(targetSpec)) ||
                (getTarget().equals(targetSpec) && getSource().equals(targetSpec));
    }

}

