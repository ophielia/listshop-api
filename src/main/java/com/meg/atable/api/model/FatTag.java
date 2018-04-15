package com.meg.atable.api.model;

import com.meg.atable.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 12/04/2018.
 */
public class FatTag {
    private final TagEntity tag ;
    private Long id;
    private Long parentId;
    private List<FatTag> children ;

    public FatTag(TagEntity tag) {
this.tag = tag;
    }

    public Long getId() {
        return tag.getId();
    }

    public String getName() {
        return tag.getName();
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void addChildren(List<FatTag> filledChildren) {
        this.children = filledChildren;
    }

    public List<FatTag> getChildren() {
        return children;
    }

    public TagType getTagType() {
        if (tag== null) {
            return null;
        }
        return tag.getTagType();
    }

    public String getDescription() {
        return tag.getDescription();
    }

    public Double getPower() {
        return tag.getPower();
    }

    public Boolean getAssignSelect() {
        return tag.getAssignSelect();
    }

    public Boolean getSearchSelect() {
        return tag.getSearchSelect();
    }

    public String getRatingFamily() {
        return tag.getRatingFamily();

    }
}
