/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.ListItemEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class CategoryDTO {

    private Long categoryId;
    private String name;
    private Integer displayOrder;
    private List<ListItemEntity> items;

    public CategoryDTO(Long categoryId, String name, Integer displayOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public List<ListItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ListItemEntity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", displayOrder=" + displayOrder +
                ", items=" + items +
                '}';
    }
}
