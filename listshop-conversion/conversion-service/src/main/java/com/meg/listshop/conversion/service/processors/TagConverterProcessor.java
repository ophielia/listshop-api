/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionUnitFactorEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.*;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import com.meg.listshop.conversion.service.handlers.TagHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Order(1)
public class TagConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(TagConverterProcessor.class);

    private List<TagHandler> tagHandlers;

    @Autowired
    public TagConverterProcessor(List<TagHandler> tagHandlers) {
        this.tagHandlers = tagHandlers;
    }

    @Override
    public void process(ProcessingContext context) {
        TagHandler handler = determineHandler(context);
        if (handler != null) {
            ConvertibleAmount amount = handler.convertTag(context);
            context.setCurrentAmount(amount);
        }
    }

    @Override
    public boolean appliesTo(ProcessingContext context) {

            LOG.debug("Checking if TagConverterProcessor applies to context: {}", context);

            if (!currentHasConversionId(context)) {
                LOG.debug("Current amount does not have conversion ID, skipping TagConverterProcessor");
                return false;
            }


            if (context.getTargetUnit() != null &&
                    context.getStartingAmount().getUnit().getId().equals(context.getTargetUnit().getId())  &&
                    sizesMatch(context))
           {
                return false;
            }


           if (currentIsSingleUnit(context)) {
                if (targetUnitExists(context) && !targetSingleUnit(context)) {
                    // should convert if target is specific unit - not a single unit
                    LOG.debug("Target unit exists and is not single unit, applying TagConverterProcessor");
                    return true;
                } else if (currentHasMarker(context)) {
                    // should convert if current has a marker - e.g. slice
                    LOG.debug("Current unit has a marker, applying TagConverterProcessor");
                    return true;
                } else if (currentOrTargetHasSize(context)) {
                    // should convert if current has a marker - e.g. slice
                    LOG.debug("Current unit has a size, applying TagConverterProcessor");
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

    private TagHandler determineHandler(ProcessingContext context) {
        return tagHandlers.stream()
                .filter(handler -> handler.shouldConvert(context))
                .findFirst()
                .orElse(null);
    }

}
