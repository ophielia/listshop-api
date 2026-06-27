/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.tag;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class StandardTagHandler extends BaseTagHandler {

    @Override
    public boolean shouldConvert(ProcessingContext context) {
        return true;
    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        return convertToMetric(context);
    }

    @Override
    public  List<ConversionFactor> findFactorsForMetric(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        // create criteria - base criteria
        Long conversionId = determineConversionId(toConvert);
        String targetSize = toConvert.getUnitSize();
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                //.withConversionId(conversionId)
                .withToUnit(GRAM_UNIT_ID);

        List<ConversionFactor> factors =  factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();

        if (factors.size() <= 1) {
            return factors;
        }

        ConversionFactor defaultFactor = factors.stream()
                .filter( f -> f.isUnitDefault())
                .map(f -> (ConversionFactor)f)
                .findFirst().orElse(null);

        if (defaultFactor == null) {
            return factors;
        } else {
            return List.of(defaultFactor);
        }
    }

    private Long determineConversionId(ConvertibleAmount toConvert) {
        UnitType unitType = toConvert.getUnit().getType();
        return List.of(UnitType.METRIC, UnitType.UK, UnitType.US).contains(unitType) ? null : toConvert.getConversionId();
    }

}
