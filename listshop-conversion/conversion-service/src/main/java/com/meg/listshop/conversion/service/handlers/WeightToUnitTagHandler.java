/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.processors.DomainConverterProcessor;
import com.meg.listshop.conversion.service.processors.ScalingProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class WeightToUnitTagHandler extends AbstractTagHandler {

    private DomainConverterProcessor domainConverterProcessor;
    private ScalingProcessor scalingProcessor;

    public WeightToUnitTagHandler(DomainConverterProcessor domainConverterProcessor, ScalingProcessor scalingProcessor) {
        this.domainConverterProcessor = domainConverterProcessor;
        this.scalingProcessor = scalingProcessor;
    }

    @Override
    public boolean shouldConvert(ProcessingContext context) {
       // return FactorRequestUtils.isFromWeight(context) &&
         //       targetIsSingleUnit(context);
return false;
    }

    @Override
    public ConvertibleAmount convertTag(ProcessingContext context) {
        //convert to grams
        return convertToGrams(context);
    }

    @Override
    public List<ConversionFactor> findFactorsForGrams(ConvertibleAmount toConvert) {
        // create criteria - for grams
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                //  .withConversionId(toConvert.getConversionId())
                .withToUnit(GRAM_UNIT_ID);
        FactorCriteria exactCriteria = criteriaBuilder.build();
        List<ConversionFactor> foundUnits = factorRepository.findFactors(exactCriteria).stream()
                .toList();

        return invertFactors(foundUnits);
    }

    private List<ConversionFactor> invertFactors(List<ConversionFactor> foundUnits) {
        return foundUnits.stream()
                .map(SimpleConversionFactor::reverseFactor)
                .map(rf -> (ConversionFactor) rf)
                .toList();
    }

}
