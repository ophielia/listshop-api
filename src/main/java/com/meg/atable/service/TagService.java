package com.meg.atable.service;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;

import java.util.Collection;
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
}
