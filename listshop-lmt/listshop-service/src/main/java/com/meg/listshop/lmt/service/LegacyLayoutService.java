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

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface LegacyLayoutService {

    ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId);

    ListLayoutEntity getDefaultUserLayout(Long userId);

    void addDefaultUserMappings(Long id, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException;

    List<ListLayoutEntity> getUserLayouts(UserEntity user);

    ListLayoutEntity getFilledStandardLayout(Long userId);


    List<ListLayoutCategoryEntity> getUserCategories(String userName);



}
