/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.conversion.service.LegacyConverterServiceImpl;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TagConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(TagConverterProcessor.class);

    @Override
    public void process(ProcessingContext context) {

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
                // should convert it target is specific unit - not a single unit
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
