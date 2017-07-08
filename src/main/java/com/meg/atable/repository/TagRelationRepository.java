package com.meg.atable.repository;

import com.meg.atable.model.Tag;
import com.meg.atable.model.TagRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRelationRepository extends JpaRepository<TagRelation,Long> {

    Optional<TagRelation> findByChild(Tag tag);

    List<TagRelation> findByParent(Tag tag);

    List<TagRelation> findByParentIsNull(Tag tag);
}