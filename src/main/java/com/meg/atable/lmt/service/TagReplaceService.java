package com.meg.atable.lmt.service;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagReplaceService {

    void replaceTag(Long toReplaceId, Long replaceWithId);

    void replaceAllTags();
}
