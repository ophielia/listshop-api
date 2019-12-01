package com.meg.atable.lmt.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ItemOperationType {
    Move("Move"),
    Remove("Remove"),
    Copy("Copy");

    private final String display;


    ItemOperationType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
