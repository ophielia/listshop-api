/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.DeltaSpec;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.domain.DomainConversionHandler;
import com.meg.listshop.conversion.service.ProcessingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Component
@Order(2)
public class DomainConverterProcessor extends AbstractConverterProcessor {
    private static
    final Logger LOG = LoggerFactory.getLogger(DomainConverterProcessor.class);
    private final List<DomainConversionHandler> handlerList;
    private final ConversionFactorRepository factorRepository;

    @Autowired
    public DomainConverterProcessor(List<DomainConversionHandler> handlerList,
                                    ConversionFactorRepository factorRepository) {
        this.handlerList = handlerList;
        this.factorRepository = factorRepository;
    }

    @Override
    public void process(ProcessingContext context) {
        ConvertibleAmount converted = convert(context);
        context.setCurrentAmount(converted);
      /*
        // find first processor for domain conversion
        DomainConversionHandler handler = findHandlerForContext(context);
       // convert current amount
       if (handler != null) {
           ConvertibleAmount amount = handler.convert(context);
           // set result in current amount
           context.setCurrentAmount(amount);
       } else {
           LOG.debug("No handler found for domain conversion from {} to {}", specFromSource(context), specFromTarget(context));
       }
*/
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
        boolean containsMetric = List.of(toConvert.getUnit().getType(),
                context.getTarget().domainType()).contains(UnitType.METRIC);
        FactorCriteria criteria = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withToDomain(context.getTarget().domainType())
                .withBridgeThroughMetric(!containsMetric)
                .withTargetDefaultUnit(true)
                .build();
        // retrieve factors
        return factorRepository.findAllFactors(criteria);
    }


    private DomainConversionHandler findHandlerForContext(ProcessingContext context) {
        DeltaSpec fromSpec = specFromSource(context);
        DeltaSpec toSpec = specFromTarget(context);

        return handlerList.stream()
                .filter(handler -> handler.appliesTo(fromSpec, toSpec))
                .findFirst()
                .orElse(null);

    }

    private DeltaSpec specFromSource(ProcessingContext context) {
        return new DeltaSpec(
                context.getCurrentAmount().getUnit().getType(),
                context.getCurrentAmount().getUnit().getId(),
                null
        );
    }

    private DeltaSpec specFromTarget(ProcessingContext context) {
        return new DeltaSpec(
                context.getTarget().domainType(),
                null,
                null
        );
    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
        UnitType currentDomain = ProcessingUtils.pullCurrentDomain(context);
        UnitType targetDomain = ProcessingUtils.pullTargetDomain(context);

        // single unit and target unit not specified => not applicable
        if (ProcessingUtils.currentIsSingleUnit(context, SINGLE_UNIT_ID) && ProcessingUtils.targetUnitNonSpecified(context)) {
            LOG.debug("Current unit is single and target unit is not specified, skipping domain conversion");
            return false;
        }

        if (currentDomain == null || targetDomain == null) {
            LOG.debug("Current or target domain is null, no domain conversion applied");
            return false;
        }
        if (currentDomain.equals(targetDomain)) {
            // no conversion to be done - domains are equal
            LOG.debug("No domain conversion to be done - domains are equal");
            return false;
        }
        // target domain exists and is convertible (UK, US, METRIC)
        UnitType nonConvertible = List.of(currentDomain, targetDomain).stream()
                .filter(not(UnitType::isConvertible))
                .findFirst().orElse(null);
        if (nonConvertible != null) {
            LOG.debug("Non-convertible domain found, no domain conversion applied: {}", nonConvertible);
            return false;
        }
        // target domain not excluded for current
        if (!context.getCurrentAmount().getUnit().isAvailableForDomain(targetDomain)) {
            LOG.debug("Target domain not available for current unit, no domain conversion applied");
            return false;
        }
        // not tag specific dish context already converted to grams
        if (context.getStartingAmount().getConversionId() != null &&
                context.getTarget().conversionContext() != null &&
                context.getCurrentAmount().getUnit().getId().equals(GRAM_UNIT_ID)) {
            LOG.debug("Tag specific, already converted to grams");
            return false;
        }


        // domains both available, different, and convertible
        LOG.debug("Domain conversion applicable: current={}, target={}", currentDomain, targetDomain);
        return true;
    }


}
