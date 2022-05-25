package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagExtendedRepository extends JpaRepository<TagExtendedEntity, Long>, TagExtendedRepositoryCustom {

    //MM refactor
    @Query(value = "select * from tag_extended " +
            "where parent_tag_id = ?1 order by power", nativeQuery = true)
    List<TagExtendedEntity> getRatingTagsForRatingType(Long ratingHeaderId);
}