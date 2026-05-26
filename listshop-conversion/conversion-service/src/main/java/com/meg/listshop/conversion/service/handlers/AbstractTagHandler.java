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
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.TagFactorRepository;
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

    @Autowired
    protected ConversionFactorRepository factorRepository;

    @Autowired
    protected TagFactorRepository tagFactorRepository;

    @Autowired
    protected UnitRepository unitRepository;

    protected ConvertibleAmount convertToMetric(@NonNull ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        List<ConversionFactor> factors = findFactorsForMetric(context);
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

}


