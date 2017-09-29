package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class TagExtended extends Tag {

    private String parentId;

    private List<String> childrenIds;


    public TagExtended() {
    }

    public TagExtended(Long id,String name, String description, Long parentId, List<Long> childrenIds) {
        super(id,name,description);

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }
}