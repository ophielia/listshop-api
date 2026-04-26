/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class ScaleUnitConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(DomainConverterProcessor.class);

    @Override
    public void process(ProcessingContext context) {

    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
        // doesn't scale for specified target unit
        if (!targetUnitNonSpecified(context)) {
            LOG.debug("target unit specified. No scaling will be done.");
            return false;
        }
        // doesn't scale for single units - either as target or current
        if (currentIsSingleUnit(context)) {
            LOG.debug("current unit is single. No scaling will be done.");
            return false;
        }
        return true;



    }
}
