/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "tag_id", "name" })
public class NestedTag {

    @JsonProperty("tag_id")
    private String tagId;

    private String name;

    @JsonProperty("tag_type")
    private String tagType;

    NestedTag() {
    }

    public NestedTag(Long tagId, String name) {
        this(tagId, name, null);
    }

    public NestedTag(Long tagId, String name, TagType tagType) {
        this.tagId = String.valueOf(tagId);
        this.name = name;
        if (tagType != null) {
            this.tagType = tagType.getDisplayName();
        }
    }



    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public String getTagType() {
        return tagType;
    }

    public NestedTag withTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public NestedTag withName(String name) {
        this.name = name;
        return this;
    }

    public NestedTag withTagType(String tagType) {
        this.tagType = tagType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NestedTag nestedTag = (NestedTag) o;
        return Objects.equals(tagId, nestedTag.tagId) && Objects.equals(name, nestedTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId='" + tagId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
