/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;

public class ProcessingContext {

    ConvertibleAmount startingAmount;
    ConvertibleAmount currentAmount;
    ConversionTarget target;
    UnitEntity targetUnit;

    public ProcessingContext(ConvertibleAmount startingAmount, ConversionTarget target) {
        this(startingAmount,target,null);
    }

    public ProcessingContext(ConvertibleAmount startingAmount, ConversionTarget target, UnitEntity targetUnit) {
        this.startingAmount = startingAmount;
        this.target = target;
        this.targetUnit = targetUnit;
        this.currentAmount = new SimpleAmount(
                startingAmount.getQuantity(),
                startingAmount.getUnit(),
                startingAmount.getConversionId(),
                startingAmount.getIsLiquid(),
                startingAmount.getMarker()
        );
    }

    public ConvertibleAmount getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(ConvertibleAmount startingAmount) {
        this.startingAmount = startingAmount;
    }

    public ConvertibleAmount getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(ConvertibleAmount currentAmount) {
        this.currentAmount = currentAmount;
    }

    public ConversionTarget getTarget() {
        return target;
    }

    public void setTarget(ConversionTarget target) {
        this.target = target;
    }

    public UnitEntity getTargetUnit() {
        return targetUnit;
    }
}
