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
@Order(4)
public class StandardTagHandler extends AbstractTagHandler {

    @Override
    public boolean shouldConvert(ProcessingContext context) {
        return true;
    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        return convertToGrams(context);
    }

    @Override
    public  List<ConversionFactor> findFactorsForGrams(ConvertibleAmount toConvert) {
        // create criteria - base criteria
        Long conversionId = determineConversionId(toConvert);
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(conversionId)
                .withToUnit(GRAM_UNIT_ID);

        return factorRepository.findFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();
    }

    @Override
    public  List<ConversionFactor> findFactorsForGrams(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        // create criteria - base criteria
        Long conversionId = determineConversionId(toConvert);
        String targetSize = toConvert.getUnitSize();
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(conversionId)
                .withToUnit(GRAM_UNIT_ID);

        if (targetSize != null) {
                criteriaBuilder = criteriaBuilder.withFromSize(targetSize);
        } else if (toConvert.getUnit().getId().equals(SINGLE_UNIT_ID)) {
            criteriaBuilder = criteriaBuilder.withFromDefaultSize(true);
        }
        return factorRepository.findFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();
    }

    private Long determineConversionId(ConvertibleAmount toConvert) {
        UnitType unitType = toConvert.getUnit().getType();
        return List.of(UnitType.METRIC, UnitType.UK, UnitType.US).contains(unitType) ? null : toConvert.getConversionId();
    }

}
