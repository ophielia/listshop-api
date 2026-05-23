/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Order(3)
public class HybridTagHandler extends AbstractTagHandler {

    @Override
    public boolean shouldConvert(ProcessingContext context) {
        // this should kick in if the from type is hybrid
        // we want the modifiers to be taken into account
        // the conversion id is used
        return fromIsHybrid(context);
    }


    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        return convertToGrams(context);
    }

    @Override
    public  List<ConversionFactor> findFactorsForGrams(ConvertibleAmount toConvert) {
        // create criteria - base criteria
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(toConvert.getConversionId())
                .withFromModifier(toConvert.getMarker())
                .withFromSize(toConvert.getUnitSize())
                .withToUnit(GRAM_UNIT_ID);
        List<ConversionFactor> factors = factorRepository.findFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();
//MM around here  - more work for markers and such
        if (!factors.isEmpty()) return factors;

        // no factors found - look for any hybrid factors available
        criteriaBuilder = new FactorCriteriaBuilder()
                .withFromType(toConvert.getUnit().getType())
                .withConversionId(toConvert.getConversionId())
                .withFromModifier(toConvert.getMarker())
                .withFromSize(toConvert.getUnitSize())
                .withToUnit(GRAM_UNIT_ID);
        List<ConversionFactor> wideFactors = factorRepository.findFactors(criteriaBuilder.build()).stream()
                .toList();
        return reverseEngineerFactor(wideFactors, toConvert);

    }

    private List<ConversionFactor> reverseEngineerFactor(List<ConversionFactor> otherUnitFactors, ConvertibleAmount toConvert) {
        if (otherUnitFactors.isEmpty()) return otherUnitFactors;
        // get first factor from wide
        ConversionFactor otherFactor = otherUnitFactors.get(0);
        // lookup factor from target unit (original from) to wide unit
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withToUnit(otherFactor.getFromUnit().getId());
        List<ConversionFactor> intermediateFactors = factorRepository.findFactors(criteriaBuilder.build()).stream()
                .toList();
        if (intermediateFactors.isEmpty()) {
            return new ArrayList<>();
        }
        // create new factor from lookup factor * wide factor, with the other broo ha ha
        ConversionFactor intermediateFactor = intermediateFactors.get(0);
        double engineeredFactor = intermediateFactor.getFactor() * otherFactor.getFactor();
        ConversionFactor created = SimpleConversionFactor.conversionFactor(toConvert.getUnit(), otherFactor.getToUnit(), engineeredFactor);
        return Collections.singletonList(created);
    }

}
