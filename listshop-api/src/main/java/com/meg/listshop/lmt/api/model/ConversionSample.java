package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversionSample {

    @JsonProperty("from_amount")
    private String fromAmount;

    @JsonProperty("from_unit")
    private String fromUnit;

    @JsonProperty("to_amount")
    private String toAmount;

    @JsonProperty("to_unit")
    private String toUnit;

    @JsonProperty("from_modifier")
    private String fromModifier;

    @JsonProperty("to_modifier")
    private String toModifier;

    public ConversionSample() {
        // empty impl for jackson
    }

    public String getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(String fromAmount) {
        this.fromAmount = fromAmount;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToAmount() {
        return toAmount;
    }

    public void setToAmount(String toAmount) {
        this.toAmount = toAmount;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    public String getFromModifier() {
        return fromModifier;
    }

    public void setFromModifier(String fromModifier) {
        this.fromModifier = fromModifier;
    }

    public String getToModifier() {
        return toModifier;
    }

    public void setToModifier(String toModifier) {
        this.toModifier = toModifier;
    }

    @Override
    public String toString() {
        return "ConversionSample{" +
                "fromAmount='" + fromAmount + '\'' +
                ", fromUnit='" + fromUnit + '\'' +
                ", toAmount='" + toAmount + '\'' +
                ", toUnit='" + toUnit + '\'' +
                ", fromModifier='" + fromModifier + '\'' +
                ", toModifier='" + toModifier + '\'' +
                '}';
    }
}
