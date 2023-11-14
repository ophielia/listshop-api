package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConversionFactorSource implements ConversionFactorSource {

    private List<ConversionFactor> factors;

    public AbstractConversionFactorSource(List<ConversionFactor> factors) {
        this.factors = factors;
    }

    @Override
    public List<ConversionFactor> getFactors(UnitType fromType) {
        List<ConversionFactor> results = new ArrayList<>();
        // go through factors
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getType().equals(fromType)) {
                results.add(factor);
            } else if (factor.getToUnit().getType().equals(fromType)) {
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
