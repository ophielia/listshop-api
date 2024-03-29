package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
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

    @Override
    public List<ConversionFactor> getFactors(Long unitId, Long tagId) {
        LOG.trace("... getting factors for unitId: [{}], oneWay: [{}]", unitId, oneWay);
        List<ConversionFactor> results = new ArrayList<>();
        // go through factors
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getId().equals(unitId)) {
                results.add(factor);
            } else if (!oneWay && factor.getToUnit().getId().equals(unitId)) {
                results.add(SimpleConversionFactor.reverseFactor(factor));
            }
        }
        return results;
    }

    @Override
    public ConversionFactor getFactor(Long fromUnitId, Long toUnitId) {
        ConversionFactor factor = factors.stream()
                .filter(f -> isExactMatch(f, fromUnitId, toUnitId))
                .findAny().orElse(null);
        if (factor == null) {
            return null;
        }
        if (factor.getToUnit().getId().equals(fromUnitId)) {
            return SimpleConversionFactor.reverseFactor(factor);
        }
        return factor;
    }

    private boolean isExactMatch(ConversionFactor factor, Long fromUnitId, Long toUnitId) {
        return (factor.getFromUnit().getId().equals(fromUnitId) &&
                factor.getToUnit().getId().equals(toUnitId)) ||
                (factor.getFromUnit().getId().equals(toUnitId) &&
                        factor.getToUnit().getId().equals(fromUnitId));
    }


}
