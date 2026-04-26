/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class ContextConverterProcessor extends AbstractConverterProcessor  {
    private static final Logger LOG = LoggerFactory.getLogger(ContextConverterProcessor.class);

    @Override
    public void process(ProcessingContext context) {

    }

    @Override
    public boolean appliesTo(ProcessingContext context) {
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
