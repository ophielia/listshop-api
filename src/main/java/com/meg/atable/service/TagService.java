package com.meg.atable.service;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    TagEntity save(TagEntity tag);

    Optional<TagEntity> getTagById(Long dishId);

    void deleteAll();

    void deleteAllRelationships();

    TagEntity createTag(TagEntity parent, String name);

    TagEntity createTag(TagEntity parent, String name, String description);

    TagEntity createTag(TagEntity parent, TagEntity newTag);

    List<TagEntity> fillInRelationshipInfo(List<TagEntity> tags);

    List<TagEntity> getTagsForDish(Long dishId);

    boolean assignTagToParent(Long tagId, Long parentId);

    boolean assignChildrenToParent(Long parentId, List<Long> childrenIds);

    public boolean assignTagToTopLevel(Long tagId);

    void addTagToDish(Long dishId, Long tagId);

    List<TagEntity> getTagList(TagFilterType baseTags, List<TagType> tagType);


    void deleteTagFromDish(Long dishId, Long tagId);

    Map<Long,TagEntity> getDictionaryForIdList(List<Long> tagIds);
}
