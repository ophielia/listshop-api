package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChainConversionHandler extends AbstractConversionHandler implements ChainConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractChainConversionHandler.class);

    private ConversionSpec source;
    private ConversionSpec target;
    private ConversionFactorSource conversionSource;


    protected AbstractChainConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source,target, conversionSource);
        this.source = source;
        this.target = target;
        this.conversionSource = conversionSource;
    }
    protected AbstractChainConversionHandler() {

    }

    public boolean convertsTo(ConversionSpec spec) {
        return target.equals(spec) || source.equals(spec);
    }
    public boolean convertsTo(UnitType domain) {
        return target.getUnitType().equals(domain) || source.getUnitType().equals(domain);
    }

    public boolean handlesDomain(UnitType sourceDomain, UnitType targetDomain) {
        return (domainTypeMatches(sourceDomain,source) && domainTypeMatches(targetDomain,target) ) ||
                (domainTypeMatches(sourceDomain,target) && domainTypeMatches(targetDomain,source) ) ;
    }

    private boolean domainTypeMatches(UnitType source, ConversionSpec compareSpec) {
        return (source.equals(compareSpec.getUnitType()));
    }
    @Override
    public ConversionSpec getSource() {
        return source;
    }
    @Override
    public ConversionSpec getTarget() {
        return target;
    }

    public ConversionFactorSource getConversionSource() {
        return conversionSource;
    }

    @Override
    public void setSource(ConversionSpec source) {
        this.source = source;
    }
    @Override
    public void setTarget(ConversionSpec target) {
        this.target = target;
    }
    @Override
    public void setConversionSource(ConversionFactorSource conversionSource) {
        this.conversionSource = conversionSource;
    }

}

