package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagOperationPut {

    @JsonProperty(value = "tag_ids")
    private List<Long> tagIds;
    @JsonProperty(value = "tag_operation_type")
    private String tagOperationType;
    @JsonProperty(value = "user_id")
    private String userId;
    @JsonProperty(value = "is_liquid")
    private Boolean isLiquid;

    public TagOperationPut() {
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public String getTagOperationType() {
        return tagOperationType;
    }

    public void setTagOperationType(String tagOperationType) {
        this.tagOperationType = tagOperationType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsLiquid() {
        return isLiquid;
    }

    public void setIsLiquid(Boolean isLiquid) {
        this.isLiquid = isLiquid;
    }
}