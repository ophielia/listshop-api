/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConversionTarget;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class SingleUnitScaler extends BaseScaleHandler {
    private static final Log LOG = LogFactory.getLog(SingleUnitScaler.class);


    public boolean shouldScale(ProcessingContext context) {
        return context.getCurrentAmount().getConversionId() != null &&
                SINGLE_UNIT_ID.equals(context.getTarget().unitId());
    }


    private ConversionFactorRepository conversionFactorRepository;

    public SingleUnitScaler(ConversionFactorRepository conversionFactorRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
    }


    public List<ConversionFactor> findFactors(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        ConversionTarget target = context.getTarget();
        // the unit factors are stored as unit => grams
        // and we need grams => units - to we'll reverse the criteria
        // to request unit => grams, and invert the results
        UnitEntity singleUnit = unitRepository.findById(SINGLE_UNIT_ID).orElse(null);

        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        FactorCriteria criteria = builder.withFromUnit(toConvert.getUnit())
                .withFromUnit(singleUnit)
                .withFromSize(target.unitSize())
                .withFromModifierOrNull(target.marker())
                .withToUnit(GRAM_UNIT_ID)
                .withFromDefaultSize(target.unitSize() == null)
                .withConversionId(toConvert.getConversionId())
                .build();
        List<ConversionFactor> factors = conversionFactorRepository.findFactors(criteria).stream()
                .map(SimpleConversionFactor::reverseFactor)
                .toList();
        if (factors.isEmpty()) {
            criteria.setFromDefaultSize(false);
            return conversionFactorRepository.findFactors(criteria).stream()
                    .map(SimpleConversionFactor::reverseFactor)
                    .toList();
        }
        return factors;
    }

}
