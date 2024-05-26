package com.meg.listshop.common;

public enum UnitType {
    US,
    METRIC,
    SPECIAL,
    UK,
    HYBRID,
    UNIT;

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
//MM need - domaintype enum
// unitdomain table, filled
// new specifier for those with unit domain

//
