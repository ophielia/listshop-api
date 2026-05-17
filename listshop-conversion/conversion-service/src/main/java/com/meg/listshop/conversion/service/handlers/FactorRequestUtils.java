/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.beans.factory.annotation.Value;

public class FactorRequestUtils {



    public static boolean fromSizeRequested(ProcessingContext context) {
        return context.getCurrentAmount().getUnitSize() != null;
    }

    public static boolean fromModifierRequested(ProcessingContext context) {
        return context.getCurrentAmount().getMarker() != null;
    }

    public static boolean isSizeOrModifierRequest(ProcessingContext context) {
        boolean fromWithSizeOrModifier =  context.getCurrentAmount().getUnitSize() != null || context.getCurrentAmount().getMarker() != null;
        boolean toWithSizeOrModifier =  context.getTarget().unitSize() != null || context.getTarget().marker() != null;
        return fromWithSizeOrModifier || toWithSizeOrModifier;
    }

    public static boolean toSizeRequested(ProcessingContext context) {
        return context.getTarget().unitSize() != null;
    }

    public static boolean toModifierRequested(ProcessingContext context) {
        return context.getTarget().marker() != null;
    }

    public static boolean isFromWeight(ProcessingContext context) {
        UnitEntity fromUnit = context.getCurrentAmount().getUnit();
        return fromUnit != null
                && fromUnit.getSubtype() == UnitSubtype.WEIGHT;
    }

}
