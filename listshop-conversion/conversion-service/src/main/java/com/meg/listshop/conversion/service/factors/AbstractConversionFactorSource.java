package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConversionFactorSource implements ConversionFactorSource {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConversionFactorSource.class);
    private final List<ConversionFactor> factors;

    private boolean oneWay;

    protected  AbstractConversionFactorSource(List<ConversionFactor> factors) {
        this.factors = factors;
    }

    protected AbstractConversionFactorSource(List<ConversionFactor> factors, boolean oneWay) {
        this.factors = factors;
        this.oneWay = oneWay;
    }

    public List<ConversionFactor> getFactors(ConvertibleAmount convertibleAmount, Long conversionId, boolean isOneWayConversion) {
        boolean einbahnStrasse = oneWay || isOneWayConversion;
        Long unitId = convertibleAmount.getUnit().getId();
        LOG.trace("... getting factors for unitId: [{}], oneWay: [{}]", unitId, einbahnStrasse);
        List<ConversionFactor> results = new ArrayList<>();
        // go through factors
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getId().equals(unitId)) {
                results.add(factor);
            } else if (!einbahnStrasse && factor.getToUnit().getId().equals(unitId)) {
                results.add(SimpleConversionFactor.reverseFactor(factor));
            }
        }
        return results;
    }

    @Override
    public ConversionFactor getFactor(Long fromUnitId, Long toUnitId, boolean isOneWayConversion) {
        ConversionFactor factor = factors.stream()
                .filter(f -> isExactMatch(f, fromUnitId, toUnitId, isOneWayConversion ))
                .findAny().orElse(null);
        if (factor == null) {
            return null;
        }
        if (factor.getToUnit().getId().equals(fromUnitId)) {
            return SimpleConversionFactor.reverseFactor(factor);
        }
        return factor;
    }

    private boolean isExactMatch(ConversionFactor factor, Long fromUnitId, Long toUnitId, boolean isOneWayConversion) {
        return (factor.getFromUnit().getId().equals(fromUnitId) &&
                factor.getToUnit().getId().equals(toUnitId)) ||
                ( !isOneWayConversion &&
                (factor.getFromUnit().getId().equals(toUnitId) &&
                        factor.getToUnit().getId().equals(fromUnitId)));
    }


}
