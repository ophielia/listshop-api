/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionUnitFactorEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.data.repository.TagFactorRepository;
import com.meg.listshop.conversion.service.ConversionTarget;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractTagHandler implements TagHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTagHandler.class);

    @Value("${listshop.single.unit.id:1011}")
    protected Long SINGLE_UNIT_ID;

    @Value("${conversionservice.gram.unit.id:1013}")
    protected Long GRAM_UNIT_ID;

    @Value("${conversionservice.milliliter.unit.id:1004}")
    protected Long MILLILITER_UNIT_ID;

    @Autowired
    protected ConversionFactorRepository factorRepository;

    @Autowired
    protected TagFactorRepository tagFactorRepository;

    @Autowired
    protected UnitRepository unitRepository;

    protected ConvertibleAmount convertToMetric(@NonNull ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        Long targetUnitId = GRAM_UNIT_ID;
        List<ConversionFactor> factors = findFactorsForMetric(context);
        ConvertibleAmount converted = convertForFactors(context, factors);

        // check liquid / grams
        if (converted.getUnit().getSubtype() == UnitSubtype.WEIGHT &&
            toConvert.getIsLiquid()) {
            // need to convert grams to milliliters
            targetUnitId = MILLILITER_UNIT_ID;
            converted = convertGramsToMilliliters(context, converted);
        }

        // put converted into context
        if (converted.getUnit().getId().equals(targetUnitId)) {
            context.setMetricMeasure(converted.getQuantity());
            context.setMetricUnit(converted.getUnit());
        }

        return new SimpleAmount(converted.getQuantity(), converted.getUnit(), toConvert, converted.getUnitSize());
    }

    private ConvertibleAmount convertGramsToMilliliters(ProcessingContext context, ConvertibleAmount converted) {
        ConversionTarget target = new ConversionTarget(UnitType.METRIC, MILLILITER_UNIT_ID,context.getTarget().conversionContext());
        ProcessingContext gramToMlContext = new ProcessingContext(converted, target, null);
        List<ConversionFactor> factors = findGramToMilliliterFactors(converted);
        return convertForFactors(gramToMlContext, factors);
    }

    private List<ConversionFactor> findGramToMilliliterFactors(ConvertibleAmount toConvert) {
        // create criteria - base criteria
        Long conversionId = toConvert.getConversionId();

        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withToUnit(MILLILITER_UNIT_ID)
                .withConversionId(conversionId);

        return  factorRepository.findFactors(criteriaBuilder.build()).stream()
                .map(factor -> (ConversionFactor) factor)
                .toList();

    }

    private ConvertibleAmount convertForFactors(ProcessingContext context, List<ConversionFactor> factors) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        Long conversionId = toConvert.getConversionId();
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No conversions available for domainConversion from unit [{}] to unitType: [{}].", toConvert.getUnit(), context.getTarget().domainType());
            return toConvert;
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit, conversionId, toConvert.getIsLiquid(), toConvert.getMarker(), f.getUnitSize(), f.isUnitDefault());
                }).collect(Collectors.toList());


        // return first result
        if (convertedList.isEmpty()) {
            return toConvert;

        }
        return convertedList.get(0);

    }

    public abstract List<ConversionFactor> findFactorsForMetric(ProcessingContext context);

    private Predicate<? super ConversionUnitFactorEntity> toMarkerFilterOrNull(ProcessingContext context) {
        String requestedMarker = context.getTarget().marker();
        return factor -> {
            if (factor.getToMarker() != null) {
                return Objects.equals(factor.getToMarker(), requestedMarker) || factor.getToMarker().isEmpty();
            }
            return true;
        };
    }

    protected boolean fromIsHybrid(ProcessingContext context) {
        return context.getCurrentAmount().getUnit().getType() == UnitType.HYBRID;
    }


    protected boolean targetIsSingleUnit(ProcessingContext context) {
        return context.getTarget().unitId() != null && Objects.equals(context.getTarget().unitId(), SINGLE_UNIT_ID);
    }

    protected boolean fromIsSingleUnit(ProcessingContext context) {
        return context.getTarget().unitId() != null && Objects.equals(context.getTarget().unitId(), SINGLE_UNIT_ID);
    }

}


