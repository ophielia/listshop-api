package com.meg.listshop.conversion.data.pojo;

import com.meg.listshop.conversion.service.FoodFactor;

public class SimpleFoodFactor implements FoodFactor {
    private Long referenceId;
    private double gramWeight;
    private double amount;

    private Long fromUnitId;

    private String marker;
    private String unitSize;
    private Boolean unitDefault;

    public SimpleFoodFactor(Long referenceId, double gramWeight, double amount, Long fromUnitId) {
         this( referenceId, gramWeight, amount, fromUnitId,null);
    }

    public SimpleFoodFactor(Long referenceId, double gramWeight, double amount, Long fromUnitId, String marker) {
        this.referenceId = referenceId;
        this.gramWeight = gramWeight;
        this.amount = amount;
        this.fromUnitId = fromUnitId;
        this.marker = marker;
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

    @Override
    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    @Override
    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    @Override
    public Boolean getUnitDefault() {
        return unitDefault;
    }

    public void setUnitDefault(Boolean unitDefault) {
        this.unitDefault = unitDefault;
    }
}
