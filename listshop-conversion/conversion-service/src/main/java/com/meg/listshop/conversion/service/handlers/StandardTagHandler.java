/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class StandardTagHandler extends AbstractTagHandler {

    @Override
    public boolean shouldConvert(ProcessingContext context) {
        return true;
    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        return standardConversion(context);
    }

    @Override
    public  List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ProcessingContext context) {
        // create criteria - base criteria
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(context.getCurrentAmount().getConversionId())
                .withToDomain(UnitType.METRIC)
                .withTargetDefaultUnit(true);
        return factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();
    }

}
