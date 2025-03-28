package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;
import java.util.Set;

public interface CustomListLayoutRepository {

    ListLayoutEntity fillLayout(Long userId, ListLayoutEntity layout);

    Long getDefaultCategoryForSiblings(Set<Long> siblings);

    Set<Long> getUserCategoriesForSiblings(Long userId, Set<Long> siblingsTagIds);
}