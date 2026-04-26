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
@Order(4)
public class SpecificUnitConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(SpecificUnitConverterProcessor.class);

    @Override
    public void process(ProcessingContext context) {

    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
        LOG.debug("in SpecificUnitConverterProcessor, specific unit [{}] found.", context.getTarget().unitId());
        return context.getTarget().unitId() != null;
    }
}
