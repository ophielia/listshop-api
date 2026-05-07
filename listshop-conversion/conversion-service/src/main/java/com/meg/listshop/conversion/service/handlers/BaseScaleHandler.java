/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.factors.NewFactorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseScaleHandler implements ScaleHandler, NewFactorProvider {
    private static final Logger LOG = LoggerFactory.getLogger(BaseScaleHandler.class);
    private static final double DEFAULT_MIN_RANGE = 0.4990;
    private static final double DEFAULT_MAX_RANGE = 500;

    public ConvertibleAmount scale(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        List<ConversionFactor> factors = findFactors(toConvert, context.getTarget());
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No factors available for scaling to [{}] from [{}]", context.getTarget(), context.getCurrentAmount().getUnit());
            return context.getCurrentAmount();
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit, f.getUnitSize());
                }).collect(Collectors.toList());
        // return right away if we only have one factor
        if (convertedList.size() == 1) {
            return convertedList.get(0);
        }

        // sort for best result, according to sort type
        ConvertibleAmount bestResult = sortForBestResult(convertedList);
        if (bestResult == null) {
            bestResult = toConvert;
        }

        // return best result
        return new SimpleAmount(bestResult.getQuantity(), bestResult.getUnit(), toConvert, bestResult.getUnitSize());

    }

    protected List<ConversionFactor> deduplicateFactors(List<ConversionFactor> allFactors, ConvertibleAmount toConvert) {
        List<ConversionFactor> cleanedFactors = new ArrayList<>();
        ConversionFactor passThrough;
        List<ConversionFactor> deduplicatedFactors = allFactors.stream()
                .collect(Collectors.toMap(factor -> factor.getToUnit().getId(),
                        factor -> factor,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
        cleanedFactors.addAll(deduplicatedFactors);
        // check for self-scaling (passthrough)
        ConversionFactor selfScalingFactor = deduplicatedFactors.stream()
                .filter(factor -> factor.getFromUnit().getId().equals(factor.getToUnit().getId()))
                .findFirst()
                .orElse(null);
        if (selfScalingFactor == null) {
            passThrough = new SimpleConversionFactor(1.0, toConvert.getUnit(), toConvert.getUnit(), null, null, null);
            cleanedFactors.add(passThrough);
        }

        return cleanedFactors;
    }


    private ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
        if (convertedList.isEmpty()) {
            return null;
        }
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
}
