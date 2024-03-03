package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.conversion.data.pojo.SimpleAmount;

public class FactorHolderDTO {

    private SimpleAmount fromAmount;

    private SimpleAmount toAmount;

    public FactorHolderDTO(SimpleAmount fromAmount, SimpleAmount toAmount) {
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    public SimpleAmount getFromAmount() {
        return fromAmount;
    }

    public SimpleAmount getToAmount() {
        return toAmount;
    }

    @Override
    public String toString() {
        return "FactorHolderDTO{" +
                "fromAmount=" + fromAmount +
                ", toAmount=" + toAmount +
                '}';
    }
}
