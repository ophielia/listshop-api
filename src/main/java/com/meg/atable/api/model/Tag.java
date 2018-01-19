package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {

    private String tag_id;

    private String name;

    private String description;

    @JsonProperty("tag_type")
    private String tagType;

    private String ratingFamily;

    private Boolean assignSelect;

    private Boolean searchSelect;

    Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long id) {
        this.tag_id = String.valueOf(id);
    }

    Tag(Long id, String name, String description, TagType tagType, String ratingFamily) {
        this.tag_id = String.valueOf(id);
        this.name = name;
        this.description = description;
        this.tagType = tagType != null ? tagType.name() : TagType.TagType.name();
        this.ratingFamily = ratingFamily;
    }

    @JsonProperty("tag_id")
    public String getId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    public Tag name(String name) {
        this.name = name;
        return this;
    }


    public String getDescription() {
        return description;
    }

    public Tag description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("tag_type")
    public String getTagType() {
        return tagType;
    }

    public Tag tagType(String tagType) {
        this.tagType = tagType;
        return this;
    }

    @JsonProperty("rating_family")
    public String getRatingFamily() {
        return ratingFamily;
    }

    public Tag ratingFamily(String ratingFamily) {
        this.ratingFamily = ratingFamily;
        return this;
    }

    @JsonProperty("assign_select")
    public Boolean getAssignSelect() {
        return assignSelect;
    }

    public Tag assignSelect(Boolean assignSelect) {
        this.assignSelect = assignSelect;
        return this;
    }

    @JsonProperty("search_select")
    public Boolean getSearchSelect() {
        return searchSelect;
    }

    public Tag searchSelect(Boolean searchSelect) {
        this.searchSelect = searchSelect;
        return this;
    }
}