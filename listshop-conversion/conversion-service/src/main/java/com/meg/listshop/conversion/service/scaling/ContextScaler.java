/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.scaling;

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
@Order(5)
public class ContextScaler extends BaseScaleHandler {

    private ConversionFactorRepository conversionFactorRepository;

    public ContextScaler(ConversionFactorRepository conversionFactorRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
    }

    public boolean shouldScale(ProcessingContext context) {
        return context.getTarget().conversionContext() != null;
    }



    public List<ConversionFactor> findFactors(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        ConversionTarget target = context.getTarget();
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        FactorCriteria criteria = builder.withFromUnit(toConvert.getUnit())
                .withToContext(target.conversionContext())
                .withToDomain(toConvert.getUnit().getType())
                .build();
        return deduplicateFactors(conversionFactorRepository.findAllFactors(criteria), toConvert);

    }

}
