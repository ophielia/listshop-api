/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.handlers.ScaleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class ScalingProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(ScalingProcessor.class);

    private List<ScaleHandler> scaleHandlerList;

    @Autowired
    public ScalingProcessor(List<ScaleHandler> scaleHandlerList) {
        this.scaleHandlerList = scaleHandlerList;
    }

    @Override
    public void process(ProcessingContext context) {
        ScaleHandler scaler = determineScaler(context);
        if (scaler != null) {
            ConvertibleAmount amount = scaler.scale(context);
            context.setCurrentAmount(amount);
        }
    }

    private ScaleHandler determineScaler(ProcessingContext context) {
        return scaleHandlerList.stream()
                .filter(scaler -> scaler.shouldScale(context))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
        // scaling processing runs if the unit is not a single unit
        boolean unitsMatch = unitsMatch(context);
        return !currentIsSingleUnit(context) &&
                !unitsMatch ||
                (unitsMatch && !sizesMatch(context));
    }

    private boolean unitsMatch(ProcessingContext context) {
        if (context.getTargetUnit() == null) {
            return false;
        }
        return context.getStartingAmount().getUnit().getId().equals(context.getTargetUnit().getId());
    }

    private boolean sizesMatch(ProcessingContext context) {
        if (context.getTarget().unitSize() == null &&
            context.getCurrentAmount().getUnitSize() == null) {
            return true;
        }
        if (context.getTarget().unitSize() != null ||
                context.getCurrentAmount().getUnitSize() != null) {
            return false;
        }

        return context.getTarget().unitSize().equals(context.getCurrentAmount().getUnitSize());
    }


    public boolean legacyAppliesTo(ProcessingContext context) {
        // single unit and target unit not specified => not applicable
        if (currentIsSingleUnit(context) && targetUnitNonSpecified(context)) {
            LOG.debug("Current unit is single and target unit is not specified, skipping conversion");
            return false;
        }

        //target context is not null
        if (targetContextNonSpecified(context)) {
            LOG.debug("Target context is not specified, skipping conversion");
            return false;
        }
        // target context is different from current
        if (contextsAreDifferent(context))  {
           LOG.debug("Target context is different from current, applying conversion");
            return true;
        }
        LOG.debug("Target context is same as current, skipping conversion");
        return false;
    }




}
