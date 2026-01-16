package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.TagType;

public class TagInfoDTO {

    private Long tagId;

    private String name;

    private String description;

    private Double power;

    private Long userId;

    private String tagType;

    private boolean isGroup;

    private Long parentId;
    private boolean toDelete;
    private Boolean isLiquid;

    public TagInfoDTO(Long tagId, String name, String description,
                      Double power, Long userId, TagType tagType,
                      boolean isGroup, Long parentId, boolean toDelete) {
        this(tagId, name, description, power, userId, tagType, isGroup, parentId, toDelete, null);
    }

    public TagInfoDTO(Long tagId, String name, String description,
                      Double power, Long userId, TagType tagType,
                      boolean isGroup, Long parentId, boolean toDelete,Boolean isLiquid) {
        this.tagId = tagId;
        this.name = name;
        this.description = description;
        this.power = power;
        this.userId = userId;
        this.tagType = tagType.name();
        this.isGroup = isGroup;
        this.parentId = parentId;
        this.toDelete = toDelete;
        this.isLiquid = isLiquid;
    }

    public Long getTagId() {
        return tagId;
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

    public Boolean getLiquid() {
        return isLiquid;
    }

    public void setLiquid(Boolean liquid) {
        isLiquid = liquid;
    }

    @Override
    public String toString() {
        return "TagInfoEntity{" +
                "tag_id=" + tagId +
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
