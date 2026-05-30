/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ProcessingContext;

public class ProcessingUtils {

    public static boolean currentIsSingleUnit(ProcessingContext context, Long singleUnitId) {
        return isSingleUnit( context.getCurrentAmount().getUnit().getId(), singleUnitId);
    }

    public static boolean currentHasMarker(ProcessingContext context) {
        return context.getCurrentAmount().getMarker() != null && context.getCurrentAmount().getMarker().trim().length() > 0;
    }
    public static boolean currentOrTargetHasSize(ProcessingContext context) {
        return context.getCurrentAmount().getUnitSize() != null ||
                context.getTarget().unitSize() != null;
    }
    public static boolean currentHasConversionId(ProcessingContext context) {
        return context.getCurrentAmount().getConversionId() != null;
    }
    public static boolean currentIsHybrid(ProcessingContext context) {
        return context.getCurrentAmount().getUnit().getType() == UnitType.HYBRID;
    }
    public static UnitType pullCurrentDomain(ProcessingContext context) {
        UnitEntity unit = context.getCurrentAmount().getUnit();
        return unit == null ? null : unit.getType();
    }
    public static double pullCurrentQuantity(ProcessingContext context) {
        return context.getCurrentAmount().getQuantity();
    }

    public static boolean targetUnitExists(ProcessingContext context) {
        return context.getTarget().unitId() != null;
    }
    public static boolean targetSingleUnit(ProcessingContext context, Long singleUnitId) {
        return ProcessingUtils.isSingleUnit( context.getTarget().unitId(), singleUnitId);
    }
    public static UnitType pullTargetDomain(ProcessingContext context) {
        return context.getTarget().domainType();
    }
    public static boolean targetUnitNonSpecified(ProcessingContext context) {
        return context.getTarget().unitId() == null;
    }
    public static boolean targetContextNonSpecified(ProcessingContext context) {
        return context.getTarget().conversionContext() != null;
    }

    public static boolean contextsAreDifferent(ProcessingContext context) {
        ConversionTargetType target = context.getTarget().conversionContext();
        UnitEntity current = context.getCurrentAmount().getUnit();
        return switch (target) {
            case Dish -> !current.isDishUnit();
            case List -> !current.isListUnit();
        };
    }

    public static boolean isSingleUnit(Long unitId, Long singleUnitId) {
        return unitId != null &&
                unitId.equals(singleUnitId);
    }
}
