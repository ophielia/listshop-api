/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.data.repository.impl.CustomConversionFactorRepositoryImpl;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.LegacyConverterServiceImpl;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(1)
public class TagConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(TagConverterProcessor.class);

    private final ConversionFactorRepository factorRepository;

    public TagConverterProcessor(ConversionFactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    @Override
    public void process(ProcessingContext context) {
        // goal is to convert current amount to metric unit - grams or ml (if liquid)
        ConvertibleAmount converted = convert(context);
        context.setCurrentAmount(converted);

        // will need to come back again for units and sizes
    }

    private ConvertibleAmount convert(@NonNull ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        List<ConversionFactor> factors = findFactors(toConvert, context);
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No conversions available for domainConversion from unit [{}] to unitType: [{}].", toConvert.getUnit(), context.getTarget().domainType());
            return toConvert;
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit, f.getUnitSize());
                }).collect(Collectors.toList());



        // return first result
        if (convertedList.isEmpty()) {
            return toConvert;

        }
        ConvertibleAmount converted = convertedList.get(0);

        return new SimpleAmount(converted.getQuantity(), converted.getUnit(), toConvert, converted.getUnitSize());
    }

    private List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ProcessingContext context) {
        // create criteria
        FactorCriteria criteria = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withToDomain(UnitType.METRIC)
                .withTargetDefaultUnit(true)
                .withConversionId(context.getCurrentAmount().getConversionId())
               // .withTargetVolumeOnly(toConvert.getUnit().getSubtype() == UnitSubtype.LIQUID)
                .build();
        // retrieve factors
        return factorRepository.findAllFactors(criteria);
    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
        LOG.debug("Checking if TagConverterProcessor applies to context: {}", context);

        if (!currentHasConversionId(context)) {
            LOG.debug("Current amount does not have conversion ID, skipping TagConverterProcessor");
            return false;
        }

        if (currentIsSingleUnit(context)) {
            if (targetUnitExists(context) && !targetSingleUnit(context)) {
                // should convert if target is specific unit - not a single unit
                LOG.debug("Target unit exists and is not single unit, applying TagConverterProcessor");
                return true;
            } else {
                // no conversion to be done - single unit can stay single unit
                LOG.debug("Current unit is single unit and target unit is single unit, skipping TagConverterProcessor");
                return false;
            }
        }
        LOG.debug("TagConverterProcessor applies to context with current unit and target unit");
        return true;
    }




}
