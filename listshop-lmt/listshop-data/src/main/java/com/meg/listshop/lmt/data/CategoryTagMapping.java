/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data;

import java.util.function.Function;

public record CategoryTagMapping(
        Long categoryId,
        String categoryName,
        Long tagId,
        String tagName
)  {
}
