package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.model.ContextType;
import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public abstract class AbstractItemCollector implements ItemCollector {
    private final Long listId;
    private final Map<Long, CollectedItem> tagCollectedItem;
    private final List<CollectedItem> freeTextItems;

    private final Predicate<CollectedItem> isChanged = i -> i.isChanged();

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

    @Override
    public void replaceOutdatedTags(List<TagEntity> outdatedTags, Map<Long,TagEntity> replacementDictionary) {
        if (outdatedTags.isEmpty()) {
            return;
        }

        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified)
                .withStatisticCountType(StatisticCountType.None)
                .build();
        for (TagEntity tag : outdatedTags) {
            // replace the tag in the tag collector
            CollectedItem toFix = getTagCollectedMap().get(tag.getId());
            TagEntity originalTag = toFix.getTag();
            TagEntity replaceTag = replacementDictionary.get(tag.getReplacementTagId());
            toFix.setTag(replaceTag);
            getTagCollectedMap().remove(originalTag.getId());
            if (getTagCollectedMap().containsKey(replaceTag.getId())) {
                addItem(toFix.getItem(), context);
            } else {
                    toFix.setChanged(true);
                    getTagCollectedMap().put(replaceTag.getId(),toFix);
                }
            }

    }

    public void addItem(ItemEntity item, CollectorContext context) {
        if (item.getTag() == null) {
            getFreeTextItemList().add(new CollectedItem(item));
            return;
        }

        addItemByTag(item.getTag(), context);
    }

    protected void addItemByTag(TagEntity tag, CollectorContext context) {
        Pair<Boolean, CollectedItem> collectedItemPair = findOrCreateItemByTagId(tag);

        CollectedItem update = collectedItemPair.getRight();
        Boolean isNew = collectedItemPair.getLeft();

        update.resetRemoved();
        update.add(context, isNew);
        getTagCollectedMap().put(tag.getId(), update);

    }

    protected Pair<Boolean, CollectedItem> findOrCreateItemByTagId(TagEntity tag) {
        CollectedItem update = getTagCollectedMap().get(tag.getId());

        if (update != null) {
            return new ImmutablePair<>(false, update);
        }

        // doesn't exist - we need to create it
        CollectedItem item = new CollectedItem(new ItemEntity());

        item.setTag(tag);
        item.setListId(getListId());
        item.setUsedCount(0);
        item.setIsAdded(true);
        Pair<Boolean, CollectedItem> collectedItemPair = new ImmutablePair<>(true, item);
        return collectedItemPair;
    }

}
