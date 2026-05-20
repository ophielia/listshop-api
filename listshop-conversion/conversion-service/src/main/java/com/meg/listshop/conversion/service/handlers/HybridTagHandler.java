/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
        return factorRepository.findFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();
    }

}
