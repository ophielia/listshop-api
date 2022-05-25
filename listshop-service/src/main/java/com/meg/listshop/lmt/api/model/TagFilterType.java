package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 06/10/2017.
 */
public enum TagFilterType {

    All("All"),
    GroupsOnly("groups"),
    NoGroups("excludeGroups"),

    ToReview("review"),
    ParentTags("ParentTags");

    private final String display;


    TagFilterType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }

}
