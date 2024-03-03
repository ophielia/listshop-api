package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.service.ConvertibleAmount;

public class ConversionSampleDTO {

    ConvertibleAmount fromAmount;
    ConvertibleAmount toAmount;

    public ConversionSampleDTO(ConvertibleAmount from, ConvertibleAmount to) {
        fromAmount = from;
        toAmount = to;
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
}