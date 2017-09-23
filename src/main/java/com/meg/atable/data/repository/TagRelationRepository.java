package com.meg.atable.data.repository;

import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRelationRepository extends JpaRepository<TagRelationEntity,Long> {

    Optional<TagRelationEntity> findByChild(TagEntity tag);

    List<TagRelationEntity> findByParent(TagEntity tag);

    List<TagRelationEntity> findByParentIsNull(TagEntity tag);
}