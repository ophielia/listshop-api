package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ListType;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class ListItemCollector implements ItemCollector {
    private final Long listId;
    private Map<Long, CollectedItem> tagCollectedItem;
    private List<CollectedItem> freeTextItems;

    private Predicate<CollectedItem> isChanged = i -> i.isChanged();

    public ListItemCollector(Long savedNewListId, List<ItemEntity> items) {
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

    public ListItemCollector(Long listId) {
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
        return tagCollectedItem.values().stream()
                .filter(isChanged)
                .map(CollectedItem::getItem)
                .collect(Collectors.toList());
    }

    // list collector
    public void addTags(List<TagEntity> tagEntityList, Long dishId, String listSource) {
        for (TagEntity tag : tagEntityList) {
            addItemByTag(tag, listSource, dishId);
        }
    }

    public void copyExistingItemsIntoList(String sourceType, List<ItemEntity> items, boolean incrementStats) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> copyOrUpdateExistingItem(item, sourceType, incrementStats));
    }

    private void copyOrUpdateExistingItem(ItemEntity item, String sourceType, boolean incrementStats) {
        // do not copy crossed off items
        if (item.getCrossedOff() != null) {
            return;
        }
        if (item.getTag() == null) {
            CollectedItem copied = copyItem(item);
            // free text item
            freeTextItems.add(copied);
        } else if (tagCollectedItem.containsKey(item.getTag().getId())) {
            CollectedItem update = tagCollectedItem.get(item.getTag().getId());
            int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
            update.setUsedCount(count + 1);
            update.addRawListSource(sourceType);
                // mark as updated, so it will be saved
            update.setUpdated(true);
            if (incrementStats) {
                update.incrementAddCount(Math.max(item.getUsedCount(), 1));
            }
            tagCollectedItem.put(item.getTag().getId(), update);
        } else {
            CollectedItem copied = copyItem(item);
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            copied.setIsAdded(true);
            copied.setUsedCount(count);
            copied.addRawListSource(sourceType);
            copied.setRawDishSources(item.getRawDishSources());
            if (incrementStats) {
                copied.incrementAddCount(Math.max(1, item.getUsedCount()));
            }
            tagCollectedItem.put(item.getTag().getId(), copied);
        }
    }

    private CollectedItem createItemFromTag(TagEntity tag, Long dishId) {
        CollectedItem item = new CollectedItem(new ItemEntity());

        item.setTag(tag);
        item.setListId(listId);
        item.addRawDishSource(dishId);
        item.setUsedCount(0);
        item.setIsAdded(true);

        return item;
    }

    private void addTagForExistingItem(CollectedItem item) {
        // if this tag has been previously removed, we need to clear that information - because
        // it's now being added.
        if (item.isDeleted()) {
            item.setDeleted(false);
        }
        item.setUpdated(true);
    }

    public void removeTagsForDish(Long dishId, List<TagEntity> tagsToRemove) {
        for (TagEntity tag : tagsToRemove) {
            removeItemByTagId(tag.getId(), dishId, false);
        }
    }

    public void removeItemsFromList(ListType listType, List<ItemEntity> items) {
        for (ItemEntity item : items) {
            removeItemWithListSource(item, listType);
        }
    }


    private CollectedItem copyItem(ItemEntity item) {
        CollectedItem copied = new CollectedItem(new ItemEntity());
        // resetting count when adding from another list
        copied.setUsedCount(item.getUsedCount());
        copied.setTag(item.getTag());
        copied.setListId(listId);
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date());
        return copied;
    }

    public void removeItemByTagId(Long tagId, Long dishId, Boolean removeEntireItem) {
        if (!tagCollectedItem.containsKey(tagId)) {
            return;
        }
        CollectedItem update = tagCollectedItem.get(tagId);

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        if (count <= 1 || removeEntireItem) {
            // delete item outright
            removeItem(update);
            return;
        } else {
            // dish is not removed - it's updated
            if (dishId != null) {
                Set<String> inflatedDishSources = FlatStringUtils.inflateStringToSet(update.getRawDishSources(), ";");
                if (inflatedDishSources.contains(String.valueOf(dishId))) {
                    inflatedDishSources.remove(String.valueOf(dishId));
                    String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources, ";");
                    update.setRawDishSources(newSources);
                }
            }
            updateItemOnRemove(update,count);
        }

        tagCollectedItem.put(tagId, update);
    }

    private void removeItemWithListSource(ItemEntity item, ListType listType) {
        if (!tagCollectedItem.containsKey(item.getTag().getId())) {
            return;
        }
        CollectedItem update = tagCollectedItem.get(item.getTag().getId());

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        if (count <= 1) {
            // delete item outright
            removeItem(update);
            return;
        } else {
            // item has other usages remaining - updated, not deleted
            if (listType != null) {
                Set<String> inflatedListSources = FlatStringUtils.inflateStringToSet(update.getRawListSources(), ";");
                if (inflatedListSources.contains(listType.name())) {
                    inflatedListSources.remove(String.valueOf(listType.name()));
                    String newSources = FlatStringUtils.flattenSetToString(inflatedListSources, ";");
                    update.setRawListSources(newSources);
                }
            }
            updateItemOnRemove(update,count);
        }

        tagCollectedItem.put(item.getTag().getId(), update);

    }

    private void removeItem(CollectedItem item) {
        item.setDeleted(true);
        item.incrementRemovedCount(item.getUsedCount());
        item.setUsedCount(0);
    }

    private void updateItemOnRemove(CollectedItem update, int count) {
        update.setUpdated(true);
        update.setUsedCount(count - 1);
        update.incrementRemovedCount();
    }

    public void addItem(ItemEntity item) {
        if (item.getTag() == null) {
            freeTextItems.add(new CollectedItem(item));
            return;
        }

        addItemByTag(item.getTag(), null, null);
    }

    private void addItemByTag(TagEntity tag, String sourceType, Long dishId) {
        CollectedItem update = tagCollectedItem.get(tag.getId());

        if (update == null) {
            update = createItemFromTag(tag, null);
        } else {
            //MM help here - check for removed
            addTagForExistingItem(update);
        }

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        update.setUsedCount(count + 1);
        update.addRawListSource(sourceType);
        update.addRawDishSource(dishId);
        update.incrementAddCount();
        tagCollectedItem.put(tag.getId(), update);

    }


    public void removeFreeTextItem(ItemEntity itemEntity) {
        // TODO implement this
    }

}
