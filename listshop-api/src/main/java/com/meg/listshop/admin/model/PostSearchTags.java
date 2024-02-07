package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PostSearchTags {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("group_include")
    private String groupIncludeType;
    @JsonProperty("include_statuses")

    private List<String> includeStatuses;

    @JsonProperty("exclude_statuses")
    private List<String> excludeStatuses;

    @JsonProperty("tag_types")
    private List<String> tagTypes;
    private String tagType;


    public PostSearchTags() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupIncludeType() {
        return groupIncludeType;
    }

    public void setGroupIncludeType(String groupIncludeType) {
        this.groupIncludeType = groupIncludeType;
    }

    public List<String> getIncludeStatuses() {
        return includeStatuses;
    }

    public void setIncludeStatuses(List<String> includeStatuses) {
        this.includeStatuses = includeStatuses;
    }

    public List<String> getExcludeStatuses() {
        return excludeStatuses;
    }

    public void setExcludeStatuses(List<String> excludeStatuses) {
        this.excludeStatuses = excludeStatuses;
    }

    @Override
    public String toString() {
        return "PostSearchTags{" +
                "userId=" + userId +
                ", tagType=" + tagType +
                ", groupIncludeType='" + groupIncludeType + '\'' +
                ", includeStatuses=" + includeStatuses +
                ", excludeStatuses=" + excludeStatuses +
                '}';
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
