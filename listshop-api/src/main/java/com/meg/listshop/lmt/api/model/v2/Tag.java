package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Dish;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("user_id")
    private String userId;

    private String name;

    private String description;

    @JsonProperty("tag_type")
    private String tagType;

    private Double power;

    @JsonProperty("is_group")
    private boolean isGroup;

    @JsonProperty("parent_id")
    private String parentId;

    Tag() {
    }

    public String getTagId() {
        return tagId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTagType() {
        return tagType;
    }

    public Double getPower() {
        return power;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public String getParentId() {
        return parentId;
    }

    public Tag withTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public Tag withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Tag withName(String name) {
        this.name = name;
        return this;
    }

    public Tag withDescription(String description) {
        this.description = description;
        return this;
    }

    public Tag withTagType(String tagType) {
        this.tagType = tagType;
        return this;
    }

    public Tag withPower(Double power) {
        this.power = power;
        return this;
    }

    public Tag withGroup(boolean isGroup) {
        this.isGroup = isGroup;
        return this;
    }

    public Tag withParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }
}
