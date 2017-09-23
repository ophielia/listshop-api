package com.meg.atable.service;

import com.meg.atable.api.model.TagInfo;
import com.meg.atable.data.entity.TagEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    TagEntity save(TagEntity tag);

    Optional<TagEntity> getTagById(Long dishId);

    Collection<TagEntity> getTagList();

    void deleteAll();

    void deleteAllRelationships();

    TagEntity createTag(TagEntity parent, String name);

    TagEntity createTag(TagEntity parent, String name, String description);

    TagInfo getTagInfo(Long tagId);

    List<TagEntity> getTagsForDish(Long dishId);

    boolean assignTagToParent(Long tagId, Long parentId);

    List<TagInfo> getTagInfoList(boolean rootOnly);

    void addTagToDish(Long dishId, Long tagId);

}
