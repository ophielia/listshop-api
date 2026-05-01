/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.DeltaSpec;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseDomainConversionHandler implements DomainConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDomainConversionHandler.class);

    private static final double DEFAULT_MIN_RANGE = 0.4990;
    private static final double DEFAULT_MAX_RANGE = 500;

    DeltaSpec source;
    DeltaSpec target;
    ConversionFactorSource conversionSource;


    @Override
    public boolean appliesTo(DeltaSpec fromSpec, DeltaSpec toSpec) {
        // checking both ways
        boolean fromMatches = sourceOrTargetMatches(fromSpec);
        boolean toMatches = sourceOrTargetMatches(toSpec);
        return fromMatches && toMatches;
    }

    @Override
    public ConvertibleAmount convert(@NonNull ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        List<ConversionFactor> factors = findFactors(toConvert);
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No conversions available for domainConversion from unit [{}] to unitType: [{}].", toConvert.getUnit(), context.getTarget().domainType());
            return toConvert;
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit, f.getUnitSize());
                }).collect(Collectors.toList());

        // sort for best result, according to sort type
        ConvertibleAmount bestResult = sortForBestResult(convertedList);
        if (bestResult == null) {
            bestResult = toConvert;
        }

        // return best result
        return new SimpleAmount(bestResult.getQuantity(), bestResult.getUnit(), toConvert, bestResult.getUnitSize());
    }

    //MM possibly a utility class?
    private ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
        if (convertedList.size() == 1) {
            return convertedList.get(0);
        }
        // sort for nearest unit first, then weed for range if required
        Comparator<ConvertibleAmount> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(1 - (f1.getQuantity()));
            Double f2ToOne = Math.abs(1 - (f2.getQuantity()));
            return f1ToOne.compareTo(f2ToOne);
        };

        convertedList.sort(comparator);
        ConvertibleAmount nearestUnitResult = convertedList.get(0);
        ConvertibleAmount best = convertedList.stream()
                .filter(a -> a.getQuantity() >= DEFAULT_MIN_RANGE && a.getQuantity() <= DEFAULT_MAX_RANGE)
                .findFirst().orElse(null);
        if (best != null) {
            return best;
        }
        return nearestUnitResult;
    }

    private List<ConversionFactor> findFactors(ConvertibleAmount toConvert) {
        List<ConversionFactor> factors = new ArrayList<>();
        factors.addAll(conversionSource.getFactors(toConvert, null, false));


        if (factors.isEmpty()) {
            String message = String.format("No factors found in handler %s.", this.getClass().getName());
            LOG.warn(message);
        }

        return factors;
    }

    private boolean sourceOrTargetMatches(DeltaSpec specToCheck) {
        return specToCheck.unitType().equals(source.unitType())
                || specToCheck.unitType().equals(target.unitType());
    }


    protected void setConversionSource(ConversionFactorSource conversionSource) {
        this.conversionSource = conversionSource;
    }

    protected void setTarget(DeltaSpec target) {
        this.target = target;
    }

    protected void setSource(DeltaSpec source) {
        this.source = source;
    }

}
