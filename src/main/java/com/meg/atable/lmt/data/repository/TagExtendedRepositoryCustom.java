package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.TagExtendedEntity;

import java.util.List;

public interface TagExtendedRepositoryCustom {

    List<TagExtendedEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean parentsOnly);
}