/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.pojo.SimpleAmount;

public class ProcessingContext {

    ConvertibleAmount startingAmount;
    ConvertibleAmount currentAmount;
    ConversionTarget target;

    public ProcessingContext(ConvertibleAmount startingAmount, ConversionTarget target) {
        this.startingAmount = startingAmount;
        this.target = target;
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
}
