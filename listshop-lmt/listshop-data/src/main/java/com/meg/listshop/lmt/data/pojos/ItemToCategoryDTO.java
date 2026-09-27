/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

public class ItemToCategoryDTO {
    private final Long tagId;
    private final String categoryName;

    public ItemToCategoryDTO(Object tagId, String categoryName) {
        this.tagId = tagId instanceof Long ? (Long) tagId : ((Number) tagId).longValue();
        this.categoryName = categoryName;
    }

    public Long tagId() {
        return tagId;
    }

    public String categoryName() {
        return categoryName;
    }
}
