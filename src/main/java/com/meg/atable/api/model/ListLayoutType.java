package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 26/10/2017.
 */
public enum ListLayoutType {

    All("All"),
    RoughGrained("RoughGrained"),
    FineGrained("FineGrained");

    private final String display;


    ListLayoutType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
