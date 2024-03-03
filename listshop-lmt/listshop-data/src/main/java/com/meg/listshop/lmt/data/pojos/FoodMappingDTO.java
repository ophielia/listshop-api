package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.TagType;

public class FoodMappingDTO {

    private Long tagId;

    private String tagName;

    private Long categoryId;

    private String categoryName;

    public FoodMappingDTO() {
    }

    public FoodMappingDTO(Long tagId, String tagName, Long categoryId, String categoryName) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "FoodMappingDTO{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}