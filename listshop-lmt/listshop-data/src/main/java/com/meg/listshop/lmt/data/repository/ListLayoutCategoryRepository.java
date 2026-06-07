/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.CategoryTagMapping;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LayoutCategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutCategoryRepository extends JpaRepository<ListLayoutCategoryEntity, Long> {
    @Query(value = "select c.category_id, c.name, c.layout_id, c.display_order, c.is_default" +
            " from list_category c " +
            "join list_layout l on l.layout_id = c.layout_id " +
            "join category_tags ct on c.category_id = ct.category_id " +
            "where l.is_default = true and tag_id = :tagId " +
            "and l.user_id is null", nativeQuery = true)
    ListLayoutCategoryEntity getStandardCategoryForTag(@Param("tagId") Long tagId);

    @Query(value = "select c.category_id, c.name, c.is_default, c.display_order" +
            " from list_category c " +
            "join list_layout l on l.layout_id = c.layout_id " +
            "join category_tags ct on c.category_id = ct.category_id " +
            "where l.is_default = true and tag_id = :tagId " +
            "and l.user_id is null", nativeQuery = true)
    List<LayoutCategoryDTO> getStandardCategories();


    @Query("select llc from ListLayoutCategoryEntity llc " +
            "where lower(trim(llc.name)) = ?1 and llc.layoutId = ?2")
    ListLayoutCategoryEntity findByNameInLayout(String name, Long layoutId);

    @Query(value = "select lc.category_id " +
            "from list_category lc " +
            "         join list_layout ll on lc.layout_id = ll.layout_id " +
            "where lc.is_default = true " +
            "  and ll.user_id is null " +
            "  and ll.is_default = true", nativeQuery = true)
    Long getDefaultCategoryId();

    @Query("select llc from ListLayoutCategoryEntity llc " +
            "where llc.categoryId in (?1)")
    List<ListLayoutCategoryEntity> getByIds(Set<Long> idsToAssign);

    @Query("select llc from ListLayoutCategoryEntity llc " +
            "where llc.categoryId in (?1)")
    List<ListLayoutCategoryEntity> getStandardCategories(Set<Long> idsToAssign);


    @Query(value = "select c.category_id, c.name, c.layout_id, c.display_order, c.is_default" +
            " from list_category c " +
            "join list_layout l on l.layout_id = c.layout_id " +
            "join category_tags ct on c.category_id = ct.category_id " +
            "where l.is_default = true and tag_id = :tagId " +
            "and l.user_id = :userId", nativeQuery = true)
    ListLayoutCategoryEntity getDefaultCategoryForTagAndUser(@Param("userId")Long userId,
                                                             @Param("tagId") Long tagId);

    @Query(value = "select ct.category_id,lc.name, ct.tag_id, t.name " +
            "from category_tags ct " +
            "join list_category lc on lc.category_id = ct.category_id " +
            "join tag t on ct.tag_id = t.tag_id " +
            "join list_layout l on l.layout_id = lc.layout_id " +
            "where l.user_id = :userId and l.layout_id = :layoutId", nativeQuery = true)
    List<CategoryTagMapping> getTagCategoryMappings(Long userId, Long layoutId);

    @Query(value = "select ct.category_id,lc.name, ct.tag_id, t.name " +
            "from category_tags ct " +
            "join list_category lc on lc.category_id = ct.category_id " +
            "join tag t on ct.tag_id = t.tag_id " +
            "join list_layout l on l.layout_id = lc.layout_id " +
            "where t.user_id = :userId and " +
            "l.is_default is not false", nativeQuery = true)
    List<CategoryTagMapping> getUserTagMappings(Long userId);

    @Query(value = "select ct.category_id,lc.name, ct.tag_id, t.name " +
            "from category_tags ct " +
            "join list_category lc on lc.category_id = ct.category_id " +
            "join tag t on ct.tag_id = t.tag_id " +
            "join list_layout l on l.layout_id = lc.layout_id " +
            "where t.user_id is null and " +
            "l.layout_id = :layoutId", nativeQuery = true)
    List<CategoryTagMapping> getStandardTagMappings(Long layoutId);
}
