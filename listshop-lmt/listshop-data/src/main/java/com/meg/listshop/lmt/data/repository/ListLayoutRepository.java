/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutRepository extends JpaRepository<ListLayoutEntity, Long>, CustomListLayoutRepository {


    @Query("select e from ListLayoutEntity e where e.userId = ?1 and e.layoutId = ?2")
    ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId);

    @Query("select e from ListLayoutEntity e where e.userId = ?1 and e.isDefault = true")
    ListLayoutEntity getDefaultUserLayout(Long userId);

    @Query("select e from ListLayoutEntity e where e.userId is null and e.isDefault = true")
    ListLayoutEntity getStandardLayout();

    @Query("select e from TagEntity e " +
            "JOIN e.categories c " +
            "where c.layoutId = ?1 " +
            "and e.tagId in (?2)")
    List<TagEntity> getTagsToDeleteFromLayout(Long layoutId, Set<Long> tagIds);

    @Query("select e from ListLayoutEntity e where e.userId = ?1")
    List<ListLayoutEntity> getUserLayouts(Long userId);

    @Query(value = """
select distinct lc.*
from list_category lc
join category_tags ct on ct.category_id = lc.category_id
join tag t on t.tag_id = ct.tag_id
join list_item it on it.tag_id = t.tag_id
where it.list_id = ?2
and lc.layout_id = ?1;
""", nativeQuery = true)
    List<ListLayoutCategoryEntity> findUserListCategoriesForList(Long userLayoutId, Long listId);

    @Query(value = """
select distinct lc.*
from list_category lc
    join list_layout ll on ll.layout_id = lc.layout_id
join category_tags ct on ct.category_id = lc.category_id
join tag t on t.tag_id = ct.tag_id
join list_item it on it.tag_id = t.tag_id
where it.list_id = ?1
and ll.user_id is null and ll.is_default = true;
""", nativeQuery = true)
    List<ListLayoutCategoryEntity> findStandardCategoriesForList( Long listId);
}
