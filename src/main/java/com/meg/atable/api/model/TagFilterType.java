package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 06/10/2017.
 */
public enum TagFilterType {

    All("All"),
    BaseTags("Base");

    private final String display;



    TagFilterType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }

}
