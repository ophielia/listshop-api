package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagOperationPut {

    @JsonProperty(value = "tag_ids")
    private List<Long> tagIds;
    @JsonProperty(value = "tag_operation_type")
    private TagOperationType tagOperationType;
    @JsonProperty(value = "user_id")
    private String userId;

    public TagOperationPut() {
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public TagOperationType getTagOperationType() {
        return tagOperationType;
    }

    public void setTagOperationType(TagOperationType tagOperationType) {
        this.tagOperationType = tagOperationType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}