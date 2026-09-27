/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.service.ConvertibleAmount;

public class ConversionSampleDTO {

    ConvertibleAmount fromAmount;
    ConvertibleAmount toAmount;
    boolean manual = false;

    public ConversionSampleDTO(ConvertibleAmount from, ConvertibleAmount to) {
        fromAmount = from;
        toAmount = to;
    }

    public static ConversionSampleDTO manualSample(ConvertibleAmount from, ConvertibleAmount to) {
        ConversionSampleDTO newSample = new ConversionSampleDTO(from, to);
        newSample.setManual(true);
        return newSample;
    }

    public ConvertibleAmount getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(ConvertibleAmount fromAmount) {
        this.fromAmount = fromAmount;
    }

    public ConvertibleAmount getToAmount() {
        return toAmount;
    }

    public void setToAmount(ConvertibleAmount toAmount) {
        this.toAmount = toAmount;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
