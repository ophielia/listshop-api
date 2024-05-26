package com.meg.listshop.conversion.data.pojo;

public enum DomainType {
    US,
    METRIC,
    UK,
    ALL;

    public static DomainType findByName(String name) {
        DomainType result = null;
        for (DomainType unitType : values()) {
            if (unitType.name().equalsIgnoreCase(name)) {
                result = unitType;
                break;
            }
        }
        return result;
    }
    }
