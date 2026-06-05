/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;

import java.util.Set;

public interface CustomListLayoutRepository {

    ListLayoutEntity fillLayout(Long userId, Long tagId, ListLayoutEntity layout);

    Long getDefaultCategoryForSiblings(Set<Long> siblings);

    Set<Long> getUserCategoriesForSiblings(Long userId, Set<Long> siblingsTagIds);
}
