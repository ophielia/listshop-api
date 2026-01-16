package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.List;

public class PostUpdateTags {

    @JsonProperty("tag_ids")
    private List<String> tagIds;

    public PostUpdateTags() {
        // empty constructor for Jackson
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public String toString() {
        return "PostUpdateTags{" +
                "tagIds=" + tagIds +
                '}';
    }
}
