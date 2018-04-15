package com.meg.atable.data.repository;

import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRelationRepository extends JpaRepository<TagRelationEntity, Long> {

    Optional<TagRelationEntity> findByChild(TagEntity tag);

    List<TagRelationEntity> findByParent(TagEntity tag);

    @Query("select e from TagRelationEntity e join fetch e.child where e.parent is null")
    List<TagRelationEntity> findByParentIsNull();

    @Query("select te FROM TagRelationEntity AS te join fetch te.child AS ch WHERE ch.tagType = ?1")
    List<TagRelationEntity> findByParentIsNullAndTagType(TagType tagType);

    @Query("select te FROM TagRelationEntity AS te join fetch te.child AS ch WHERE ch.tagType = ?1")
    List<TagRelationEntity> findByParentIsNullAndTagTypeIn(List<TagType> tagType);

    @Query("select te FROM TagRelationEntity AS te join fetch te.child AS ch WHERE ch.tagType = ?1")
    List<TagRelationEntity> findByTagTypeIn(List<TagType> tagType);

    @Query(value = "select tr.parent_tag_id, tr.child_tag_id " +
            "       from tag_relation tr " +
            "       join tag t on t.tag_id = tr.child_tag_id " +
            "       where t.tag_type in (:tagTypes);", nativeQuery = true)
    List<Object[]> getTagRelationshipsForTagType(@Param("tagTypes") List<String> tagTypes);

    @Query(value = "select tr.parent_tag_id, tr.child_tag_id " +
            "       from tag_relation tr ;", nativeQuery = true)
    List<Object[]> getAllTagRelationships();

}