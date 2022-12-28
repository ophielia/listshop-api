package com.meg.listshop.lmt.service;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

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

    ListLayoutEntity getFilledDefaultLayout();

    void assignDefaultCategoryToTag(List<TagEntity> siblings, TagEntity tagToAssign);

    void assignUserDefaultCategoriesToTag(List<TagEntity> siblings, TagEntity tagToAssign);

    List<ListLayoutCategoryEntity> getUserCategories(String userName);
}
