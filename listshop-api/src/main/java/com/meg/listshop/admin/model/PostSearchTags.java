package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.List;

public class PostSearchTags {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("text_fragment")
    private String textFragment;


    @JsonProperty("group_include")
    private String groupIncludeType;
    @JsonProperty("included_statuses")
    private List<String> includeStatuses;

    @JsonProperty("excluded_statuses")
    private List<String> excludeStatuses;

    @JsonProperty("tag_types")
    private List<TagType> tagTypes;


    public PostSearchTags() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public List<TagType> getTagTypes() {
        return tagTypes;
    }

    public void setTagTypes(List<TagType> tagTypes) {
        this.tagTypes = tagTypes;
    }

    public String getTextFragment() {
        return textFragment;
    }

    public void setTextFragment(String textFragment) {
        this.textFragment = textFragment;
    }

    @Override
    public String toString() {
        return "PostSearchTags{" +
                "userId=" + userId +
                ", textFragment=" + textFragment +
                ", groupIncludeType='" + groupIncludeType + '\'' +
                ", includeStatuses=" + includeStatuses +
                ", excludeStatuses=" + excludeStatuses +
                '}';
    }


}
