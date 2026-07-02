/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("is_group")
    private Boolean isGroup;

    private Double power;

    @JsonProperty("parent_id")
    private String parentId;

    public Tag() {
        // empty constructor
    }

    public Tag(Long tagId) {
        this.tagId = String.valueOf(tagId);
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

    public Tag withIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
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

    public Tag withParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId='" + tagId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tagType='" + tagType + '\'' +
                ", power=" + power +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
