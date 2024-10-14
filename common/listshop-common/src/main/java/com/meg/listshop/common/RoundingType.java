package com.meg.listshop.common;

public enum RoundingType {
    UNIT (1.0),
    HUNDREDTH (100.0),
    THOUSANDTH(1000.0),
    EIGHTH(8.0),
    THIRD(3.0),
    QUARTER(4.0);

    private final double coefficient;
    RoundingType(double coefficient) {
        this.coefficient = coefficient;
    }

    double round(double amount) {
        return Math.round(amount * coefficient) / coefficient;
    }

    double roundUp(double amount) {
        return Math.round(Math.ceil(amount * coefficient)) / coefficient;
    }
}
