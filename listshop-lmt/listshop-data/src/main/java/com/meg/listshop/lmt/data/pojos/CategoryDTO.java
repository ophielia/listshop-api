/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.ListItemEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class CategoryDTO {

    private Long categoryId;
    private String name;
    private Integer displayOrder;
    private List<ListItemDTO> items;

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

    public String getComparisonName() {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public List<ListItemDTO> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ListItemDTO> items) {
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
