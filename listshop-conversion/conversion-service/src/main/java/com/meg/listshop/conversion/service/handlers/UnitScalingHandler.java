package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class UnitScalingHandler extends AbstractScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UnitScalingHandler.class);


    public UnitScalingHandler() {

        setConversionSource(null);
        setScalerType(ConversionTargetType.List);
        setSkipNoConversionRequiredCheck(true);
    }


    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {
        List<ConversionFactor> factors = reverseIfNecessary(context.getUnitConversionFactors());
        if (factors.isEmpty()) {
            return new ArrayList<>();
        }
        if (factors.size() == 1) {
            return factors;
        }

        // get target unit size
        String targetSize = context.getTargetUnitSize();

        // return default, if targetSize is not set
        if (targetSize == null) {
            // return default (first, because there may be more than one)
            return returnDefaultFactor(factors);

        }

        ConversionFactor targetSizeFactor = factors.stream()
                .filter(f -> f.getUnitSize() != null && f.getUnitSize().equals(targetSize))
                .findFirst()
                .orElse(null);
        if (targetSizeFactor != null) {
            return Collections.singletonList(targetSizeFactor);
        }
        return returnDefaultFactor(factors);
    }

    private List<ConversionFactor> returnDefaultFactor(List<ConversionFactor> factors) {
        ConversionFactor defaultFactor = factors.stream()
                .filter(ConversionFactor::isUnitDefault)
                .findFirst()
                .orElse(null);
        if (defaultFactor != null) {
            return Collections.singletonList(defaultFactor);
        } else if (!factors.isEmpty()) {
            return Collections.singletonList(factors.get(0));
        }
        return Collections.emptyList();
    }

    private List<ConversionFactor> reverseIfNecessary(List<ConversionFactor> factors) {
        List<ConversionFactor> reversedFactors = new ArrayList<>();
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getType().equals(UnitType.UNIT)) {
                // reverse
                reversedFactors.add(SimpleConversionFactor.reverseFactor(factor));
            } else {
                reversedFactors.add(factor);
            }
        }
        return reversedFactors;
    }
}

