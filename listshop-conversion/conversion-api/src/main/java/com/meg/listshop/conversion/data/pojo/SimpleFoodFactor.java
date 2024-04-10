package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.service.FoodFactor;

public class SimpleFoodFactor implements FoodFactor {
    private Long referenceId;
    private double gramWeight;
    private double amount;

    private Long fromUnitId;

    public SimpleFoodFactor(Long referenceId, double gramWeight, double amount, Long fromUnitId) {
        this.referenceId = referenceId;
        this.gramWeight = gramWeight;
        this.amount = amount;
        this.fromUnitId = fromUnitId;
    }

    @Override
    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public double getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public Long getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(Long fromUnitId) {
        this.fromUnitId = fromUnitId;
    }
}
