package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ItemOperationType {
    Move("Move"),
    Remove("Remove"),
    Copy("Copy"),
    RemoveAll("RemoveAll"),
    RemoveCrossedOff("RemoveCrossedOff");

    private final String display;


    ItemOperationType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
