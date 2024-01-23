package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

import java.util.Collections;
import java.util.List;

@Deprecated
public abstract class AbstractOneWayConversionHandler extends AbstractConversionHandler {


    protected AbstractOneWayConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source, target, conversionSource);
    }

    protected AbstractOneWayConversionHandler() {
    }

    @Override
    public boolean convertsTo(ConversionSpec spec) {
        return getTarget().equals(spec);
    }

    @Override
    public boolean handles(ConversionSpec sourceSpec, ConversionSpec targetSpec) {
        return (getSource().equals(sourceSpec) && getTarget().equals(targetSpec)) ||
                (getTarget().equals(targetSpec) && getSource().equals(targetSpec));
    }

    @Override
    public List<ConversionSpec> getAllSources() {
        return Collections.singletonList(getSource());
    }

}

