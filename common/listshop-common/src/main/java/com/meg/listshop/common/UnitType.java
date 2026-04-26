/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.common;

public enum UnitType {
    US(true),
    METRIC(true),
    SPECIAL(false),
    UK(true),
    HYBRID(false),
    UNIT(false);

    private final boolean isConvertible;

    UnitType(boolean isConvertible) {
        this.isConvertible = isConvertible;
    }

    public boolean isConvertible() {
        return isConvertible;
    }

    public static UnitType findByName(String name) {
        UnitType result = null;
        for (UnitType unitType : values()) {
            if (unitType.name().equalsIgnoreCase(name)) {
                result = unitType;
                break;
            }
        }
        return result;
    }
}

