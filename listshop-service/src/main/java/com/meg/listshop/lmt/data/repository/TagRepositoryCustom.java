package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.StandardUserTagConflictDTO;

import java.util.List;
import java.util.Set;

public interface TagRepositoryCustom {

    List<TagEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean assignSelect, Boolean searchSelect);

    Long findRatingTagIdForStep(Long ratingId, Integer step);


    List<StandardUserTagConflictDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys);
}