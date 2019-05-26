package com.meg.atable.lmt.service;

import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.api.model.ListType;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public abstract class AbstractItemCollector implements ItemCollector {
    private final Long listId;
    private Map<Long, CollectedItem> tagCollectedItem;
    private List<CollectedItem> freeTextItems;

    private Predicate<CollectedItem> isChanged = i -> i.isChanged();

    public AbstractItemCollector(Long savedNewListId, List<ItemEntity> items) {
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

    public AbstractItemCollector(Long listId) {
        this.listId = listId;
        tagCollectedItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }

    @Override
    public List<ItemEntity> getAllItems() {
        return Stream.concat(tagCollectedItem.values().stream()
                ,freeTextItems.stream())
        .map(CollectedItem::getItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollectedItem> getCollectedTagItems() {
        return tagCollectedItem.values()
                .stream().filter(i -> i.getTag() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllTagIds() {
        return tagCollectedItem.values().stream()
                .map(i -> i.getItem().getTag().getId())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasChanges() {
        Optional<CollectedItem> changeOpt = tagCollectedItem.values().stream()
                .filter(isChanged)
                .findFirst();

        return changeOpt.isPresent();
    }

    @Override
    public List<ItemEntity> getChangedItems() {
        return getTagCollectedMap().values().stream()
                .filter(isChanged)
                .map(CollectedItem::getItem)
                .collect(Collectors.toList());
    }

    public Long getListId() {
        return listId;
    }

    public Map<Long, CollectedItem> getTagCollectedMap() {
        return tagCollectedItem;
    }

    public List<CollectedItem> getFreeTextItemList() {
        return freeTextItems;
    }


}
