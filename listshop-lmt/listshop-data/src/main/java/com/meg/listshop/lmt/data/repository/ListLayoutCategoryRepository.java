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
    @Query(value = "select c.category_id, c.name, c.layout_id, c.display_order, c.is_default" +
            " from list_category c " +
            "join list_layout l on l.layout_id = c.layout_id " +
            "join category_tags ct on c.category_id = ct.category_id " +
            "where tag_id = :tagId " +
            "and l.user_id is null", nativeQuery = true)
    ListLayoutCategoryEntity getStandardCategoryForTag(@Param("tagId") Long tagId);

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
}
