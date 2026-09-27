/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data;

public record CategoryTagMapping(
        Long categoryId,
        String categoryName,
        Long tagId,
        String tagName
) {
}
