package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;

import java.util.List;

public interface ItemCollector {
    List<ItemEntity> getAllItems();

    List<CollectedItem> getCollectedTagItems();

    List<Long> getAllTagIds();

    boolean hasChanges();

    List<ItemEntity> getChangedItems();
}
