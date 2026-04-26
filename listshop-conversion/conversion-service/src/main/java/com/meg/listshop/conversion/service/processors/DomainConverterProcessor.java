/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.function.Predicate.not;

@Component
@Order(2)
public class DomainConverterProcessor extends AbstractConverterProcessor  {
        private static final Logger LOG = LoggerFactory.getLogger(DomainConverterProcessor.class);

    @Override
    public void process(ProcessingContext context) {

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

    protected UnitType pullCurrentDomain(ProcessingContext context) {
        UnitEntity unit = context.getCurrentAmount().getUnit();
        return unit == null ? null : unit.getType();
    }

    protected UnitType pullTargetDomain(ProcessingContext context) {
        return context.getTarget().domainType();
    }

}
