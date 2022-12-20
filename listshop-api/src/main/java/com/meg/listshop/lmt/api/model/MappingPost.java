package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class MappingPost {
    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("tag_ids")
    private List<String> tagIds;

    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTagIds() {
        return tagIds;
    }


    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public String toString() {
        return "MappingPost{" +
                "categoryId='" + categoryId + '\'' +
                ", tagIds=" + tagIds +
                '}';
    }


}
