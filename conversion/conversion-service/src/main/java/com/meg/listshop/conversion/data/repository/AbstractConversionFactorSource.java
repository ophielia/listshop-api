package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConversionFactorSource implements ConversionFactorSource {

    private List<ConversionFactor> factors;

    private boolean oneWay;
    public AbstractConversionFactorSource(List<ConversionFactor> factors) {
        this.factors = factors;
    }

    public AbstractConversionFactorSource(List<ConversionFactor> factors, boolean oneWay) {
        this.factors = factors;
        this.oneWay = oneWay;
    }

    @Override
    public List<ConversionFactor> getFactors(Long unitId) {
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
        return ((factor.getFromUnit().getId().equals(fromUnitId) &&
                factor.getToUnit().getId().equals(toUnitId))) ||
                (factor.getFromUnit().getId().equals(toUnitId) &&
                        factor.getToUnit().getId().equals(fromUnitId));
    }


}
