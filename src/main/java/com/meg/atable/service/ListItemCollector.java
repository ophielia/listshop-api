package com.meg.atable.service;

import com.meg.atable.api.model.ListType;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class ListItemCollector {
    private final Long listId;
    private Map<Long, ItemEntity> tagToItem;
    private List<ItemEntity> freeTextItems;


    public ListItemCollector(Long savedNewListId, List<ItemEntity> items) {
        this.listId = savedNewListId;
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
        if (items != null) {
            items.stream().forEach(item -> {
                if (item.getTag() != null) {
                    tagToItem.put(item.getTag().getId(), item);
                } else {
                    freeTextItems.add(item);
                }
            });
        }
    }

    public ListItemCollector(Long listId) {
        this.listId = listId;
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }


    public List<ItemEntity> getAllItems() {
        return Stream.concat(tagToItem.values().stream()
                .filter(i -> !i.isDeleted()), freeTextItems.stream())
                .collect(Collectors.toList());
    }

    public List<ItemEntity> getTagItems() {
        return tagToItem.values()
                .stream().filter(i -> i.getTag() != null)
                .collect(Collectors.toList());
    }

    public List<Long> getAllTagIds() {
        return tagToItem.values().stream()
                .map(i -> i.getTag().getId())
                .collect(Collectors.toList());
    }

    public List<ItemEntity> getItemsToUpdate() {
        return tagToItem.values().stream()
                .filter(ItemEntity::isUpdated)
                .collect(Collectors.toList());
    }

    public List<ItemEntity> getItemsToDelete() {
        return tagToItem.values().stream()
                .filter(ItemEntity::isDeleted)
                .collect(Collectors.toList());
    }

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
            ItemEntity copied = copyItem(item);
            // free text item
            freeTextItems.add(copied);
        } else if (tagToItem.containsKey(item.getTag().getId())) {
            ItemEntity update = tagToItem.get(item.getTag().getId());
            int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
            update.setUsedCount(count + 1);
            update.addRawListSource(sourceType);
            if (incrementStats) {
                update.incrementAddCount(Math.max(item.getUsedCount(), 1));
            } else {
                // just mark as updated, so it will be saved
                update.setUpdated(true);
            }
            tagToItem.put(item.getTag().getId(), update);
        } else {
            ItemEntity copied = copyItem(item);
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            copied.setUsedCount(count);
            copied.addRawListSource(sourceType);
            copied.setRawDishSources(item.getRawDishSources());
            if (incrementStats) {
                copied.incrementAddCount(Math.max(1, item.getUsedCount()));
            } else {
                // just mark as updated, so it will be saved
                copied.setUpdated(true);
            }
            tagToItem.put(item.getTag().getId(), copied);
        }
    }

    private void createItemFromTag(TagEntity tag, Long dishId) {
        ItemEntity item = new ItemEntity();
        item.setTag(tag);
        item.setListId(listId);
        item.addRawDishSource(dishId);
        item.setUsedCount(0);
        item.setAddedOn(new Date());

        tagToItem.put(tag.getId(), item);
    }

    private void addTagToItem(Long tagid, Long dishId) {
        ItemEntity item = tagToItem.get(tagid);
        item.setUsedCount(item.getUsedCount() + 1);
        item.addRawDishSource(dishId);
        item.setAddedOn(new Date());
        tagToItem.put(tagid, item);
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


    private ItemEntity copyItem(ItemEntity item) {
        ItemEntity copied = new ItemEntity();
        copied.setUsedCount(item.getUsedCount()); // MM resetting count when adding from another list
        copied.setTag(item.getTag());
        copied.setListId(listId);
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date()); // MM also need to think about this
        return copied;
    }

    public void removeItemByTagId(Long tagId, Long dishId, Boolean removeEntireItem) {
        if (!tagToItem.containsKey(tagId)) {
            return;
        }
        ItemEntity update = tagToItem.get(tagId);

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        if (count <= 1 || removeEntireItem) {
            // delete item outright
            update.setDeleted(true);
            update.incrementRemovedCount(update.getUsedCount());
            update.setUsedCount(0);
            return;
        } else {
            if (dishId != null) {
                Set<String> inflatedDishSources = FlatStringUtils.inflateStringToSet(update.getRawDishSources(), ";");
                if (inflatedDishSources.contains(String.valueOf(dishId))) {
                    inflatedDishSources.remove(String.valueOf(dishId));
                    String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources, ";");
                    update.setRawDishSources(newSources);
                }
            }
            update.setUsedCount(count - 1);
            update.incrementRemovedCount();
        }

        tagToItem.put(tagId, update);
    }

    private void removeItemWithListSource(ItemEntity item, ListType listType) {
        if (!tagToItem.containsKey(item.getTag().getId())) {
            return;
        }
        ItemEntity update = tagToItem.get(item.getTag().getId());

        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        if (count <= 1) {
            // delete item outright
            update.setDeleted(true);
            update.incrementRemovedCount(update.getUsedCount());
            update.setUsedCount(0);
            return;
        } else {
            if (listType != null) {
                Set<String> inflatedListSources = FlatStringUtils.inflateStringToSet(update.getRawListSources(), ";");
                if (inflatedListSources.contains(listType.name())) {
                    inflatedListSources.remove(String.valueOf(listType.name()));
                    String newSources = FlatStringUtils.flattenSetToString(inflatedListSources, ";");
                    update.setRawListSources(newSources);
                }
            }
            update.setUsedCount(count - 1);
            update.incrementRemovedCount();
        }

        tagToItem.put(item.getTag().getId(), update);

    }


    public void addItem(ItemEntity item) {
        if (item.getTag() == null) {
            freeTextItems.add(item);
            return;
        }

        addItemByTag(item.getTag(), null, null);
    }

    private void addItemByTag(TagEntity tag, String sourceType, Long dishId) {
        if (!tagToItem.containsKey(tag.getId())) {
            createItemFromTag(tag, null);
        }
        ItemEntity update = tagToItem.get(tag.getId());


        int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
        update.setUsedCount(count + 1);
        update.addRawListSource(sourceType);
        update.addRawDishSource(dishId);
        update.incrementAddCount();
        tagToItem.put(tag.getId(), update);

    }


    public void removeFreeTextItem(ItemEntity itemEntity) {
        // MM implement this
    }

}
