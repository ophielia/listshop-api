package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

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
            "order by display_order ", nativeQuery = true)
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
            "order by display_order ", nativeQuery = true)
    List<ListLayoutCategoryEntity> getSubcategoriesAbove(Long layoutId, Long parentId,int displayOrder);


    @Query(value = "select * " +
            "from list_category lc " +
            "  join category_relation cr on cr.child_category_id = lc.category_id " +
            "where lc.layout_id = ?1 " +
            "  and cr.parent_category_id = ?2 " +
            "  and lc.display_order < ?3 " +
            "order by display_order desc", nativeQuery = true)
    List<ListLayoutCategoryEntity> getSubcategoriesBelow(Long layoutId, Long parentId,int displayOrder);

    @Query(value = "select tr.category_id, tr.tag_id " +
            "       from category_tags tr " +
            "       join tag t on t.tag_id = tr.tag_id " +
            "       join list_category c on c.category_id = tr.category_id " +
            "       where t.tag_id in (:tagIds)" +
            "       and c.layout_id = :layoutId;", nativeQuery = true)
    List<Object[]> getTagRelationshipsForIds(@Param("tagIds") Set<Long> tagIds, @Param("layoutId") Long layoutId);


    List<ListLayoutCategoryEntity> findByTagsContains(TagEntity tagEntity);

    List<ListLayoutCategoryEntity> findByLayoutIdEquals(Long layoutId);

    List<ListLayoutCategoryEntity> findByIsDefaultTrue();

    @Query(value = "select * from list_category lc\n" +
            "         join list_layout ll on lc.layout_id = ll.layout_id\n" +
            "         where is_default = true\n" +
            "and ll.layout_type = :layoutType", nativeQuery = true)
    ListLayoutCategoryEntity findDefaultForLayoutType(@Param("layoutType") String layoutType);

    @Query(value = "select tag_id, category_id " +
            "from category_tags ct " +
            "join tag t using (tag_id) " +
            "join list_category c using (category_id) " +
            "where c.layout_id = :listLayoutId and t.tag_id in (:tagIds);", nativeQuery = true)
    List<Object[]> getTagToCategoryRelationshipsForTagIds(@Param("tagIds") Set<Long> tagIds, @Param("listLayoutId") Long listLayoutId);

    @Query(value = "select item_id, c.category_id  " +
            "from category_tags ct  " +
            "join tag t on t.tag_id = ct.tag_id " +
            "join list_item li on li.tag_id = t.tag_id " +
            "join list_category c on c.category_id = ct.category_id  " +
            "where c.layout_id = :layoutId and item_id in (:tagIds);", nativeQuery = true)
    List<Object[]> getItemToCategoryRelationshipsForItemIds(@Param("tagIds") Set<Long> tagIds, @Param("layoutId") Long layoutId);
}
