package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface ListLayoutService {

    @Deprecated
    ListLayoutCategoryEntity getDefaultListCategory();

    ListLayoutEntity getListLayoutById(Long listLayoutId);

    List<TagEntity> getTagsForLayoutCategory(Long layoutCategoryId);

    List<ListLayoutCategoryEntity> getCategoriesForTag(TagEntity tag);

    void addTagsToCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    void addTagToCategory(Long layoutCategoryId, TagEntity tag);

    List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId);

    ListLayoutCategoryEntity getLayoutCategoryForTag(Long listLayoutId, Long tagId);

    ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId);

    ListLayoutEntity getDefaultUserLayout(Long userId);

    ListLayoutEntity getStandardLayout();

}
