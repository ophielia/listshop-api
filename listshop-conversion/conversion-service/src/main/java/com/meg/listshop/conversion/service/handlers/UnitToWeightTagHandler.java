/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class UnitToWeightTagHandler extends AbstractTagHandler {

    @Override
    public boolean shouldConvert(ProcessingContext context) {
     return false;
        //   return fromIsSingleUnit(context);

    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        return convertToGrams(context);
    }

    @Override
    public List<ConversionFactor> findFactorsForGrams(ConvertibleAmount toConvert) {
        // create criteria - base criteria
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(toConvert.getConversionId())
                .withToUnit(GRAM_UNIT_ID);
        return factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                .toList();
    }

    @Override
    public List<ConversionFactor> findFactorsForGrams(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        // create criteria - base criteria
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(toConvert.getConversionId())
                .withToUnit(GRAM_UNIT_ID);
        return factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                .toList();
    }
}
