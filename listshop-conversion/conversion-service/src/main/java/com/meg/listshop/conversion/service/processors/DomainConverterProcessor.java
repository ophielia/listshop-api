/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.DeltaSpec;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.handlers.DomainConversionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.function.Predicate.not;

@Component
@Order(2)
public class DomainConverterProcessor extends AbstractConverterProcessor  {
        private static final Logger LOG = LoggerFactory.getLogger(DomainConverterProcessor.class);
    private final List<DomainConversionHandler> handlerList;

    @Autowired
    public DomainConverterProcessor(List<DomainConversionHandler> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public void process(ProcessingContext context) {
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
        UnitType currentDomain = pullCurrentDomain(context);
        UnitType targetDomain = pullTargetDomain(context);

        // single unit and target unit not specified => not applicable
        if (currentIsSingleUnit(context) && targetUnitNonSpecified(context)) {
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
        // domains both available, different, and convertible
        LOG.debug("Domain conversion applicable: current={}, target={}", currentDomain, targetDomain);
        return true;
    }


}
