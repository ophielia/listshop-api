package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.List;
import java.util.Map;

public interface ItemCollector {
    List<ItemEntity> getAllItems();

    List<CollectedItem> getCollectedTagItems();

    List<Long> getAllTagIds();

    boolean hasChanges();

    List<ItemEntity> getChangedItems();

    void replaceOutdatedTags(List<TagEntity> outdatedTags, Map<Long, TagEntity> replacementDictionary);
}
