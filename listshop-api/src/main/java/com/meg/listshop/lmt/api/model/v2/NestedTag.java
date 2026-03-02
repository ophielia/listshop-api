package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedTag {

    @JsonProperty("tag_id")
    private String tagId;

    private String name;

    NestedTag() {
    }

    public NestedTag(String tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }

    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public NestedTag withTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public NestedTag withName(String name) {
        this.name = name;
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
