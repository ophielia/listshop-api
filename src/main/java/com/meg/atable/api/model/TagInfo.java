package com.meg.atable.api.model;

import com.meg.atable.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagInfo {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private List<Long> siblingIds;
    private List<Long> childrenIds;

    public TagInfo(TagEntity tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.description = tag.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Long> getSiblingIds() {
        return siblingIds;
    }

    public void setSiblingIds(List<Long> siblingIds) {
        this.siblingIds = siblingIds;
    }

    public List<Long> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<Long> childrenIds) {
        this.childrenIds = childrenIds;
    }
}
