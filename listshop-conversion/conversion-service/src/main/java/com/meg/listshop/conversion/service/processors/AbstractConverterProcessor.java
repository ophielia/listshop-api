/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractConverterProcessor implements ConverterProcessor {

    @Value("${conversionservice.gram.unit.id:1013}")
    protected Long GRAM_UNIT_ID;

    @Value("${conversionservice.single.unit.id:1011}")
    protected Long SINGLE_UNIT_ID;

    protected boolean currentIsSingleUnit(ProcessingContext context) {
        return isSingleUnit( context.getCurrentAmount().getUnit().getId());
    }

    protected boolean currentHasConversionId(ProcessingContext context) {
        return context.getCurrentAmount().getConversionId() != null;
    }

    protected boolean targetUnitExists(ProcessingContext context) {
        return context.getTarget().unitId() != null;
    }

    protected boolean targetSingleUnit(ProcessingContext context) {
        return isSingleUnit( context.getTarget().unitId());
    }

    protected UnitType pullCurrentDomain(ProcessingContext context) {
        UnitEntity unit = context.getCurrentAmount().getUnit();
        return unit == null ? null : unit.getType();
    }

    protected UnitType pullTargetDomain(ProcessingContext context) {
        return context.getTarget().domainType();
    }

    protected boolean targetUnitNonSpecified(ProcessingContext context) {
        return context.getTarget().unitId() == null;
    }

    protected boolean targetContextNonSpecified(ProcessingContext context) {
        return context.getTarget().conversionContext() != null;
    }

    protected boolean contextsAreDifferent(ProcessingContext context) {
        ConversionTargetType target = context.getTarget().conversionContext();
        UnitEntity current = context.getCurrentAmount().getUnit();
        return switch (target) {
            case Dish -> !current.isDishUnit();
            case List -> !current.isListUnit();
        };
    }


    private boolean isSingleUnit(Long unitId) {
        return unitId != null &&
                unitId.equals(SINGLE_UNIT_ID);
    }

}
