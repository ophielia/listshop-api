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
public class ListItemCollector extends AbstractItemCollector {

    public ListItemCollector(Long listId, List<ItemEntity> items) {
        super(listId, items);
    }

    public ListItemCollector(Long listId) {
        super(listId);
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
            getFreeTextItemList().add(copied);
        } else if (getTagCollectedMap().containsKey(item.getTag().getId())) {
            CollectedItem update = getTagCollectedMap().get(item.getTag().getId());
            int count = update.getUsedCount() != null ? update.getUsedCount() : 0;
            update.setUsedCount(count + 1);
            update.addRawListSource(sourceType);
                // mark as updated, so it will be saved
            update.setUpdated(true);
            if (incrementStats) {
                update.incrementAddCount(Math.max(item.getUsedCount(), 1));
            }
            getTagCollectedMap().put(item.getTag().getId(), update);
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
            getTagCollectedMap().put(item.getTag().getId(), copied);
        }
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
        copied.setListId(getListId());
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date());
        return copied;
    }

    public void removeItemByTagId(Long tagId, Long dishId, Boolean removeEntireItem) {
        if (!getTagCollectedMap().containsKey(tagId)) {
            return;
        }
        CollectedItem update = getTagCollectedMap().get(tagId);

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

        getTagCollectedMap().put(tagId, update);
    }

    private void removeItemWithListSource(ItemEntity item, ListType listType) {
        if (!getTagCollectedMap().containsKey(item.getTag().getId())) {
            return;
        }
        CollectedItem update = getTagCollectedMap().get(item.getTag().getId());

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

        getTagCollectedMap().put(item.getTag().getId(), update);

    }

    private void removeItem(CollectedItem item) {
        item.setRemoved(true);
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
            getFreeTextItemList().add(new CollectedItem(item));
            return;
        }

        addItemByTag(item.getTag(), null, null);
    }

    private void addItemByTag(TagEntity tag, String sourceType, Long dishId) {
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


    public void removeFreeTextItem(ItemEntity itemEntity) {
        // TODO implement this
    }

}
