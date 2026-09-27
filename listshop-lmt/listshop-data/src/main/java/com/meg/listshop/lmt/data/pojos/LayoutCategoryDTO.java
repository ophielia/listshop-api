/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.CategoryTagMapping;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LayoutCategoryDTO {

    private Long categoryId;

    private String categoryName;

    private Boolean isDefault;

    private Integer displayOrder;

    private Long linkedCategoryId;

    private List<CategoryTagMapping> tags = new ArrayList<>();


    public LayoutCategoryDTO(ListLayoutCategoryEntity listLayoutCategoryEntity) {
        this.categoryId = listLayoutCategoryEntity.getId();
        this.categoryName = listLayoutCategoryEntity.getName();
        this.isDefault = listLayoutCategoryEntity.getDefault();
        this.displayOrder = listLayoutCategoryEntity.getDisplayOrder();
        this.linkedCategoryId = listLayoutCategoryEntity.getLinkedCategoryId();
    }

    public LayoutCategoryDTO(Long categoryId, String categoryName, Boolean isDefault, Integer displayOrder, Long linkedCategoryId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isDefault = isDefault;
        this.displayOrder = displayOrder;
        this.linkedCategoryId = linkedCategoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getLinkedCategoryId() {
        return linkedCategoryId;
    }

    public List<CategoryTagMapping> getTags() {
        return tags;
    }

    public void setTags(List<CategoryTagMapping> tags) {
        this.tags = tags;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public String toString() {
        return "LayoutCategoryDTO{" +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", linkedCategoryId=" + linkedCategoryId +
                '}';
    }

    public void addTagMapping(CategoryTagMapping value) {
        this.tags.add(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LayoutCategoryDTO that = (LayoutCategoryDTO) o;
        return Objects.equals(categoryId, that.categoryId) && Objects.equals(categoryName, that.categoryName) && Objects.equals(linkedCategoryId, that.linkedCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName, linkedCategoryId);
    }
}
