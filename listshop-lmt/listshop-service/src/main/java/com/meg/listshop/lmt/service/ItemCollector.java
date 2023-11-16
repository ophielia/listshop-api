package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;
import java.util.Map;

public interface ItemCollector {
    List<ListItemEntity> getAllItems();

    List<CollectedItem> getCollectedTagItems();

    List<Long> getAllTagIds();

    boolean hasChanges();

    List<ListItemEntity> getChangedItems();

    void replaceOutdatedTags(List<TagEntity> outdatedTags, Map<Long, TagEntity> replacementDictionary);
}
