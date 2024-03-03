package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 06/10/2017.
 */
public enum TagFilterType {

    All("All"),  // will be handled differently - two pulls - structure and tags
    GroupsOnly("groups"), // will be handled with group include type
    NoGroups("excludeGroups"), // will be handled with group include type

    ToReview("review"), // will be handled with internal status
    ParentTags("ParentTags");  // dont know what this does ?=??  actually, no usages found in api or in extranet

    private final String display;


    TagFilterType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }

}
