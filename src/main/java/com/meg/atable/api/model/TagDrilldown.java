package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagDrilldown extends Tag {

    @JsonProperty("parent_id")
    private String parentId;
    private List<TagDrilldown> children;


    TagDrilldown() {
    }

    public TagDrilldown(String name) {
        super(name);
    }

    public TagDrilldown(Long id) {
        super(id);

    }

    TagDrilldown(Long id, String name, String description, TagType tagType, String ratingFamily) {
        super(id, name, description, tagType, ratingFamily);
    }

    public String getParentId() {
        return parentId;
    }

    public TagDrilldown parentId(String parent_id) {
        this.parentId = parent_id;
        return this;
    }

    public List<TagDrilldown> getChildren() {
        return children;
    }

    public TagDrilldown childrenList(List<TagDrilldown> children) {
        this.children = children;
        return this;
    }
}