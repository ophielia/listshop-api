package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChainConversionHandler extends AbstractConversionHandler implements ChainConversionHandler {

    private ConversionSpec source;
    private ConversionSpec target;


    protected AbstractChainConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        super(source,target, conversionSource);
        this.source = source;
        this.target = target;
    }
    protected AbstractChainConversionHandler() {

    }


    public boolean convertsToDomain( UnitType targetDomain) {
        return (domainTypeMatches(targetDomain,source) || domainTypeMatches(targetDomain,target) );
    }
    public boolean handlesDomain(UnitType sourceDomain, UnitType targetDomain) {
        return (domainTypeMatches(sourceDomain,source) && domainTypeMatches(targetDomain,target) ) ||
                (domainTypeMatches(sourceDomain,target) && domainTypeMatches(targetDomain,source) ) ;
    }

    private boolean domainTypeMatches(UnitType source, ConversionSpec compareSpec) {
        return source.equals(compareSpec.getUnitType());
    }
    @Override
    public ConversionSpec getSource() {
        return source;
    }
    @Override
    public ConversionSpec getTarget() {
        return target;
    }


    @Override
    public void setSource(ConversionSpec source) {
        this.source = source;
    }
    @Override
    public void setTarget(ConversionSpec target) {
        this.target = target;
    }

    public ConversionSpec getOppositeSource(UnitType unitType) {
        if (unitType == getSource().getUnitType()) {
            return getTarget();
        }
        return getSource();
    }

}

