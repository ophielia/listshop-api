package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListShopCategory;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface ListLayoutService {
    List<ListLayoutEntity> getListLayouts();

    ListLayoutEntity createListLayout(ListLayoutEntity listLayoutEntity);

    ListLayoutCategoryEntity getDefaultListCategory();

    ListLayoutEntity getListLayoutById(Long listLayoutId);

    ListLayoutEntity getDefaultListLayout();

    void deleteListLayout(Long listLayoutId);

    Long addCategoryToListLayout(Long listLayoutId, ListLayoutCategoryEntity entity);

    void deleteCategoryFromListLayout(Long listLayoutId, Long layoutCategoryId) throws ListLayoutException;

    ListLayoutCategoryEntity updateListLayoutCategory(Long listLayoutId, ListLayoutCategoryEntity listLayoutCategory);

    List<TagEntity> getUncategorizedTagsForList(Long listLayoutId);

    List<TagEntity> getTagsForLayoutCategory(Long layoutCategoryId);

    List<ListLayoutCategoryEntity> getCategoriesForTag(TagEntity tag);

    void addTagsToCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    void addTagToCategory(Long layoutCategoryId, TagEntity tag);

    void deleteTagsFromCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId);

    List<ListLayoutCategoryPojo> getStructuredCategories(ListLayoutEntity listLayout);

    void structureCategories(Map<Long, ListShopCategory> filledCategories, Long listLayoutId, boolean pruneSubcategories);

    void addCategoryToParent(Long categoryId, Long parentId) throws ListLayoutException;

    void moveCategory(Long categoryId, boolean moveUp) throws ListLayoutException;


    void assignTagToDefaultCategories(TagEntity newtag);

    ListLayoutCategoryEntity getLayoutCategoryForTag(Long listLayoutId, Long tagId);
}
