package com.meg.listshop.common;

public enum SharedPropertyKeys {
    PREFERRED_DOMAIN;

    public static SharedPropertyKeys findByName(String name) {
        SharedPropertyKeys result = null;
        for (SharedPropertyKeys unitType : values()) {
            if (unitType.name().equalsIgnoreCase(name)) {
                result = unitType;
                break;
            }
        }
        return result;
    }
}

