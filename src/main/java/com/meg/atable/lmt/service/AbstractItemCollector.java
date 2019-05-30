package com.meg.atable.lmt.service;

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

    @Override
    public void replaceOutdatedTags(List<TagEntity> outdatedTags, Map<Long,TagEntity> replacementDictionary) {
        if (outdatedTags.isEmpty()) {
            return;
        }
            for (TagEntity tag : outdatedTags) {
                // replace the tag in the tag collector
                CollectedItem toFix = getTagCollectedMap().get(tag.getId());
                TagEntity originalTag = toFix.getTag();
                TagEntity replaceTag = replacementDictionary.get(tag.getReplacementTagId());
                toFix.setTag(replaceTag);
                getTagCollectedMap().remove(originalTag.getId());
                if (getTagCollectedMap().containsKey(replaceTag.getId())) {
                    addItem(toFix.getItem());
                } else {
                    toFix.setChanged(true);
                    getTagCollectedMap().put(replaceTag.getId(),toFix);
                }
            }

    }

    public void addItem(ItemEntity item) {
        if (item.getTag() == null) {
            getFreeTextItemList().add(new CollectedItem(item));
            return;
        }

        addItemByTag(item.getTag(), null, null);
    }

    protected void addItemByTag(TagEntity tag, String sourceType, Long dishId) {
        CollectedItem update = getTagCollectedMap().get(tag.getId());

        if (update == null) {
            update = createItemFromTag(tag, null);
        } else {
            addTagForExistingItem(update);
        }

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        update.setUsedCount(count + 1);
        update.addRawListSource(sourceType);
        update.addRawDishSource(dishId);
        update.incrementAddCount();
        getTagCollectedMap().put(tag.getId(), update);

    }

    private CollectedItem createItemFromTag(TagEntity tag, Long dishId) {
        CollectedItem item = new CollectedItem(new ItemEntity());

        item.setTag(tag);
        item.setListId(getListId());
        item.addRawDishSource(dishId);
        item.setUsedCount(0);
        item.setIsAdded(true);

        return item;
    }


    private void addTagForExistingItem(CollectedItem item) {
        // if this tag has been previously removed, we need to clear that information - because
        // it's now being added.
        if (item.isRemoved()) {
            item.setRemoved(false);
        }
        item.setUpdated(true);
    }
}
