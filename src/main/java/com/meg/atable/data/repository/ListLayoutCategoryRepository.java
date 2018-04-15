package com.meg.atable.data.repository;

import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutCategoryRepository extends JpaRepository<ListLayoutCategoryEntity, Long> {


    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id = ?2 " +
            "order by display_order desc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getSubcategoriesForOrder(Long layoutId , Long parentId);

    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id is null " +
            "order by display_order desc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getCategoriesForOrder(Long layoutId);

    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id is null " +
            "  and lc.display_order > ?2 " +
            "order by display_order asc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getCategoriesAbove(Long layoutId, int displayOrder);


    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id is null " +
            "  and lc.display_order < ?2 " +
            "order by display_order desc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getCategoriesBelow(Long layoutId, int displayOrder);

    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id = ?2 " +
            "  and lc.display_order > ?3 " +
            "order by display_order asc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getSubcategoriesAbove(Long layoutId, Long parentId,int displayOrder);


    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id = ?2 " +
            "  and lc.display_order < ?3 " +
            "order by display_order desc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getSubcategoriesBelow(Long layoutId, Long parentId,int displayOrder);

    List<ListLayoutCategoryEntity> findByTagsContains(TagEntity tagEntity);

    List<ListLayoutCategoryEntity> findByLayoutIdEquals(Long layoutId);
}
