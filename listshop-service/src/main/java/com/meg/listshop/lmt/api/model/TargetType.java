package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 09/10/2017.
 */
public enum TargetType {

    Standard("Standard"),
    PickUp("PickUp");

    private final String display;



    TargetType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }


}
