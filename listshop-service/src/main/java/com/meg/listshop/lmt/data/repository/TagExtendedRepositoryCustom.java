package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;

import java.util.List;

public interface TagExtendedRepositoryCustom {

    List<TagExtendedEntity> findTagsByCriteria(List<TagType> tagTypes);
}