package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.List;

public interface TagRepositoryCustom {

    List<TagEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean assignSelect, Boolean searchSelect);
}