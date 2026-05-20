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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class GenericScaler extends BaseScaleHandler {
    private static final Log LOG = LogFactory.getLog(GenericScaler.class);


    public boolean shouldScale(ProcessingContext context) {
        // this scale will apply if no others apply
        return true;
    }


    private ConversionFactorRepository conversionFactorRepository;

    public GenericScaler(ConversionFactorRepository conversionFactorRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
    }


    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionTarget target) {
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        FactorCriteria criteria = builder.withFromUnit(toConvert.getUnit())
                .withToDomain(target.domainType())
                .build();
        return deduplicateFactors(conversionFactorRepository.findAllFactors(criteria), toConvert);
    }}
