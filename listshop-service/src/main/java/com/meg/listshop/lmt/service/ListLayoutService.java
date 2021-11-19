package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.model.Category;
import com.meg.listshop.lmt.api.model.ListLayoutType;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface ListLayoutService {
    List<ListLayoutEntity> getListLayouts();

    ListLayoutEntity getListLayoutByType(ListLayoutType listLayoutType);

    ListLayoutEntity createListLayout(ListLayoutEntity listLayoutEntity);

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

    void deleteTagsFromCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    List<ListLayoutCategoryEntity> getListCategoriesForIds(Set<Long> categoryIds);

    List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId);

    List<Category> getStructuredCategories(ListLayoutEntity listLayout);

    void structureCategories(Map<Long, Category> filledCategories, Long listLayoutId, boolean pruneSubcategories);

    void addCategoryToParent(Long categoryId, Long parentId) throws ListLayoutException;

    void moveCategory(Long categoryId, boolean moveUp) throws ListLayoutException;


    List<Pair<TagEntity, ListLayoutCategoryEntity>> getTagCategoryChanges(Long listLayoutId, Date changedAfter);

    List<Pair<ItemEntity, ListLayoutCategoryEntity>> getItemChangesWithCategories(Long listLayoutId, List<ItemEntity> changedItems);

    void assignTagToDefaultCategories(TagEntity newtag);

    ListLayoutCategoryEntity getLayoutCategoryForTag(Long listLayoutId, Long tagId);
}
