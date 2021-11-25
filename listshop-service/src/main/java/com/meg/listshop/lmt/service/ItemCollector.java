package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

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
