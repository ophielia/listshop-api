/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LayoutCategoryDTO;
import com.meg.listshop.lmt.data.pojos.LayoutDTO;

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface LayoutService {

    ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId);

    ListLayoutEntity getDefaultUserLayout(Long userId);

    ListLayoutEntity getStandardLayout();

    void addDefaultUserMappings(Long id, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException;

    List<ListLayoutEntity> getUserLayouts(UserEntity user);

    ListLayoutEntity getFilledStandardLayout(Long userId);

    void assignDefaultCategoryToTag(List<TagEntity> siblings, TagEntity tagToAssign);

    void assignUserDefaultCategoriesToTag(List<TagEntity> siblings, TagEntity tagToAssign);

    List<ListLayoutCategoryEntity> getUserCategories(String userName);

    List<ListLayoutCategoryEntity> getUserCategoriesForList(Long userLayoutId, Long listId);

    List<ListLayoutCategoryEntity> getStandardCategoriesForList(Long listId);

    List<LayoutCategoryDTO> getDefaultCategories();

    void addTagToCategory(Long layoutCategoryId, TagEntity tag);

    void moveTagToDefaultCategory(Long tagId, Long categoryId);

    List<ListLayoutEntity> getAllLayouts(Long userId);

    LayoutDTO getStandardLayout(Long userId);

    List<ListLayoutEntity> getAllLayoutsWithTag(Long userId, Long tagId);

    ListLayoutCategoryEntity getCategoryForTag(Long userId, Long tagId);

}
