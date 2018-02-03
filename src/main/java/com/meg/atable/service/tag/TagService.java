package com.meg.atable.service.tag;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.tag.impl.StandardTagChangeListener;

import java.util.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    TagEntity save(TagEntity tag);

    Optional<TagEntity> getTagById(Long dishId);


    TagEntity createTag(TagEntity parent, String name);

    TagEntity createTag(TagEntity parent, String name, String description);

    TagEntity createTag(TagEntity parent, TagEntity newTag);

    List<TagEntity> getTagsForDish(Long dishId);

    boolean assignTagToParent(Long tagId, Long parentId);

    boolean assignChildrenToParent(Long parentId, List<Long> childrenIds);

    boolean assignTagToParent(TagEntity childTag, TagEntity newParentTag);

    void addTagToDish(Long dishId, Long tagId);

    List<TagEntity> getTagList(TagFilterType baseTags, List<TagType> tagType);


    void deleteTagFromDish(Long dishId, Long tagId);

    Map<Long,TagEntity> getDictionaryForIds(Set<Long> tagIds);

    TagEntity updateTag(Long tagId, TagEntity toUpdate);

    void replaceTagInDishes(String name, Long fromTagId, Long toTagId);

    void addTagChangeListener(TagChangeListener tagChangeListener);


}
