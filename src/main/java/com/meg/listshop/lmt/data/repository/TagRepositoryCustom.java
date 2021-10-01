package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;

public interface TagRepositoryCustom {

    List<TagEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean assignSelect, Boolean searchSelect);

    Long findRatingTagIdForStep(Long ratingId, Integer step);


}