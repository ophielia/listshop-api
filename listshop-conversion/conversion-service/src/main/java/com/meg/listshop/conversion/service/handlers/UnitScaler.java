/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConversionTarget;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class UnitScaler extends BaseScaleHandler {

    private ConversionFactorRepository conversionFactorRepository;

    public UnitScaler(ConversionFactorRepository conversionFactorRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
    }

    public boolean shouldScale(ProcessingContext context) {
        // applies if the conversion is to one specific unit
        return context.getTarget().unitId() != null;
    }


    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionTarget target) {

        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        FactorCriteria criteria = builder.withFromUnit(toConvert.getUnit())
                .withToUnit(target.unitId())
                .build();
        return conversionFactorRepository.findFactors(criteria);
    }

}
