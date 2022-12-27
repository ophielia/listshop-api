package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;

import java.util.List;
import java.util.Set;

public interface CustomListLayoutRepository {

    List<ListLayoutEntity> getFilledUserLayouts(Long userId);
    ListLayoutEntity getFilledDefaultLayout();

    Long getDefaultCategoryForSiblings(Set<Long> siblings);

    Set<Long> getUserCategoriesForSiblings(Long userId, Set<Long> siblingsTagIds);
}