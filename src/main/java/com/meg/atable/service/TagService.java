package com.meg.atable.service;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import com.meg.atable.model.TagInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    Tag save(Tag tag);

    Optional<Tag> getTagById(Long dishId);

    Collection<Tag> getTagList();

    void deleteAll();

    void deleteAllRelationships();

    Tag createTag(Tag parent, String name);

    Tag createTag(Tag parent, String name, String description);

    TagInfo getTagInfo(Long tagId);

    List<Tag> getTagsForDish(Long dishId);

    boolean assignTagToParent(Long tagId, Long parentId);

    List<TagInfo> getTagInfoList(boolean rootOnly);

    void addTagToDish(Long dishId, Long tagId);

}
