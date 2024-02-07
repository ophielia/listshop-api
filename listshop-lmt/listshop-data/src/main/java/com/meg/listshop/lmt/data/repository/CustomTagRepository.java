package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;

import java.util.List;
import java.util.Set;

public interface CustomTagRepository {

    public List<TagEntity> findTagsByCriteria(TagSearchCriteria criteria);

    Long findRatingTagIdForStep(Long ratingId, Integer step);

    List<LongTagIdPairDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys);


}