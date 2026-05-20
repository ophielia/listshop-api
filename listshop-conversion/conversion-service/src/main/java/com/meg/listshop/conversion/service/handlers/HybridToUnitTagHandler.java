/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConversionTarget;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.processors.DomainConverterProcessor;
import com.meg.listshop.conversion.service.processors.ScalingProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class HybridToUnitTagHandler extends AbstractTagHandler {

    //MM THIS IS NEW!!  It should handle as the name says
    // find factors should use unit_factors - with both directions (not manual invert)

    private DomainConverterProcessor domainConverterProcessor;
    private ScalingProcessor scalingProcessor;

    public HybridToUnitTagHandler(DomainConverterProcessor domainConverterProcessor, ScalingProcessor scalingProcessor) {
        this.domainConverterProcessor = domainConverterProcessor;
        this.scalingProcessor = scalingProcessor;
    }

    @Override
    public boolean shouldConvert(ProcessingContext context) {
        Long toUnitId = context.getTarget().unitId();
        boolean toHybrid = false;
        if (toUnitId != null) {
            UnitEntity toUnit = unitRepository.findById(toUnitId).orElse(null);
            toHybrid = toUnit!=null && toUnit.getType().equals(UnitType.HYBRID);
        }

        return toHybrid || FactorRequestUtils.isFromHybrid(context);

    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        // then run standard conversion (standard factors)
        return standardConversion(context);
    }

    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ProcessingContext context) {
        // create criteria - for unit to weight - and then we'll reverse them by hand afterwards
        String targetSize = context.getTarget().unitSize();
        boolean useDefaultUnit = targetSize == null;
        UnitEntity fromUnit = unitRepository.findById(context.getTarget().unitId()).orElseThrow();
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(fromUnit)
                .withConversionId(context.getCurrentAmount().getConversionId())
                .withToUnit(toConvert.getUnit().getId())
                .withTargetDefaultSize(useDefaultUnit);
        FactorCriteria exactCriteria = criteriaBuilder.build();
        return   tagFactorRepository.findAllFactors(exactCriteria).stream()
                .map( f -> (ConversionFactor) f)
                .toList();


    }

}
