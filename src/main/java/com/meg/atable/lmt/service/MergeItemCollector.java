package com.meg.atable.lmt.service;

import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.api.model.ListType;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.time.Duration;
import java.time.Period;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class MergeItemCollector extends AbstractItemCollector {


    public MergeItemCollector(Long listId, List<ItemEntity> items) {
        super(listId, items);
    }

    public MergeItemCollector(Long listId) {
        super(listId);
    }

    // merge collector
    public void addMergeItems(List<ItemEntity> mergeItems) {
        if (mergeItems == null || mergeItems.isEmpty()) {
            return;
        }
        // go through all merge items
        Iterator<ItemEntity> mergeIterator = mergeItems.iterator();
        while (mergeIterator.hasNext()) {
            ItemEntity mergeItemEntity = mergeIterator.next();
            Long tagId = mergeItemEntity.getTag().getId();
            if (getTagCollectedMap().containsKey(tagId)) {
                CollectedItem serverItem = getTagCollectedMap().get(mergeItemEntity.getTag().getId());
                CollectedItem mergeItem = new CollectedItem(mergeItemEntity);
                // if merge item matches an item in the tagCollectedItems
                mergeItem.setFromClient(true);
                //  check change
                if (!serverItem.equalsWithWindow(2,mergeItem)) {  //MM parameterize this window second
                    //  if change merge
                    CollectedItem merged = mergeChangedItems(serverItem, mergeItem);
                    merged.setChanged(true);
                    getTagCollectedMap().put(tagId, merged);
                }
                //  remove from iterator
                mergeIterator.remove();
            }
        }

        // go through unmatched merge items, adding them to the list
        for (ItemEntity newMergeItem : mergeItems ) {
            // create new collected item
            CollectedItem item = new CollectedItem(newMergeItem);

            // set added date, and changed
            item.setIsAdded(true);

            // add to TagCollectedMap
            if (newMergeItem.getTag() != null) {
                getTagCollectedMap().put(newMergeItem.getTag().getId(), item);
            } else {
                getFreeTextItemList().add(item);
            }
        }

    }

    private CollectedItem mergeChangedItems(CollectedItem serverItem, CollectedItem mergeItem) {
        Duration period = Duration.between(serverItem.getStatusDate(), mergeItem.getStatusDate());

        if (period.isNegative()) {
            return serverItem;
        }
        return mergeItem;
    }






    /*
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

    private CollectedItem copyItem(CollectedItem fromItem, CollectedItem toItem) {
        CollectedItem copied = new CollectedItem(new ItemEntity());
        // resetting count when adding from another list
        copied.setUsedCount(item.getUsedCount());
        copied.setTag(item.getTag());
        copied.setListId(getListId());
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date());
        return copied;
    }

    public void addItem(ItemEntity item) {
        if (item.getTag() == null) {
            getFreeTextItemList().add(new CollectedItem(item));
            return;
        }

        addItemByTag(item.getTag(), null, null);
    }

    public void removeFreeTextItem(ItemEntity itemEntity) {

    }

     */

}
