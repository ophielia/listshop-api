/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;

import java.util.ArrayList;
import java.util.List;

public class LayoutDTO {

private Long id;
private String name;
private Boolean isDefault;
private Long userId;
private List<LayoutCategoryDTO> categories = new ArrayList<>();

    public LayoutDTO(Long id, String name, Boolean isDefault, Long userId, List<LayoutCategoryDTO> categories) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.userId = userId;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public Long getUserId() {
        return userId;
    }

    public List<LayoutCategoryDTO> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return "LayoutDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                ", userId=" + userId +
                ", categories=" + categories.size() +
                '}';
    }
}
