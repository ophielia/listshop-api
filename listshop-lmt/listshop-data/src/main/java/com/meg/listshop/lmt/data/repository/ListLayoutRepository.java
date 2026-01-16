package com.meg.listshop.lmt.data.repository;


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
}
