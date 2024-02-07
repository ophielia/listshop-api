package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.TagType;

public class TestTagInfo {

    private Long tag_id;

    private String name;

    private String description;

    private Double power;

    private Long userId;

    private String tagType;

    private boolean isGroup;

    private Long parentId;
    private boolean toDelete;

    public TestTagInfo(Long tagId, String name, String description,
                       Double power, Long userId, TagType tagType,
                       boolean isGroup, Long parentId, boolean toDelete) {
        this.tag_id = tagId;
        this.name = name;
        this.description = description;
        this.power = power;
        this.userId = userId;
        this.tagType = tagType.name();
        this.isGroup = isGroup;
        this.parentId = parentId;
        this.toDelete = toDelete;
    }

    public Long getTagId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPower() {
        return power;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTagType() {
        return tagType;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public Long getParentId() {
        return parentId;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    @Override
    public String toString() {
        return "TagInfoEntity{" +
                "tag_id=" + tag_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", power=" + power +
                ", userId=" + userId +
                ", tagType=" + tagType +
                ", isGroup=" + isGroup +
                ", parentId=" + parentId +
                ", toDelete=" + toDelete +
                '}';
    }
}