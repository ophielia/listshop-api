package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class TagExtended extends Tag {

    private String parentId;

    private List<String> childrenIds;

    public TagExtended() {
    }

    public TagExtended(Long id, String name, String description, TagType tagType, String ratingFamily, Long parentId, List<Long> childrenIds, boolean searchSelect, boolean assignSelect) {
        super(id, name, description, tagType, ratingFamily);
        super.searchSelect(searchSelect);
        super.assignSelect((assignSelect));
        if (parentId != null) {
            this.parentId = String.valueOf(parentId);
        } else {
            this.parentId = "0";
        }
        if (childrenIds != null) {
            this.childrenIds = childrenIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }
    }

    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("children_ids")
    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }


}