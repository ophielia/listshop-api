package com.meg.listshop.lmt.data.pojos;

public enum IncludeType {

    ONLY("ONLY"),
    EXCLUDE("EXCLUDE"),
    IGNORE("IGNORE");

    private final String display;
    IncludeType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
