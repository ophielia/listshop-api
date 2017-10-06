package com.meg.atable.data.repository;

import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRelationRepository extends JpaRepository<TagRelationEntity,Long> {

    Optional<TagRelationEntity> findByChild(TagEntity tag);

    List<TagRelationEntity> findByParent(TagEntity tag);

    @Query("select e from TagRelationEntity e join fetch e.child where e.parent is null")
    List<TagRelationEntity> findByParentIsNull();
}