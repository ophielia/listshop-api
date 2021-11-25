package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 06/10/2017.
 */
public enum TagFilterType {

    All("All"),
    ForSelectAssign("ForSelect"),
    ForSelectSearch("ForSearchSelect"),
    //BaseTags("Base"),
    ParentTags("ParentTags");

    private final String display;


    TagFilterType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }

}
