package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class MergeItemCollector implements ItemCollector {

    private final Long listId;
    private Map<Long, CollectedItem> tagCollectedItem;
    private List<CollectedItem> freeTextItems;

    private Predicate<CollectedItem> isChanged = i -> i.isChanged();

    public MergeItemCollector(Long savedNewListId, List<ItemEntity> items) {
        this.listId = savedNewListId;
        tagCollectedItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
        if (items != null) {
            items.stream().forEach(item -> {
                if (item.getTag() != null) {
                    tagCollectedItem.put(item.getTag().getId(), new CollectedItem(item));
                } else {
                    freeTextItems.add(new CollectedItem(item));
                }
            });
        }
    }

    public MergeItemCollector(Long listId) {
        this.listId = listId;
        tagCollectedItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }


    @Override
    public List<ItemEntity> getAllItems() {
        return null;
    }

    @Override
    public List<CollectedItem> getCollectedTagItems() {
        return null;
    }

    @Override
    public List<Long> getAllTagIds() {
        return null;
    }

    @Override
    public boolean hasChanges() {
        return false;
    }

    @Override
    public List<ItemEntity> getChangedItems() {
        return null;
    }
}
