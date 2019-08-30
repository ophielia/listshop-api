package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

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
            if (tagId.equals(151L)) {
                Long beep = mergeItemEntity.getTag().getId();
            }
            if (getTagCollectedMap().containsKey(tagId)) {
                CollectedItem serverItem = getTagCollectedMap().get(mergeItemEntity.getTag().getId());
                CollectedItem mergeItem = new CollectedItem(mergeItemEntity);
                // if merge item matches an item in the tagCollectedItems
                mergeItem.setFromClient(true);
                //  check change
                if (!serverItem.equalsWithWindow(2,mergeItem)) {  //MM parameterize this window second
                    //  if change merge
                    CollectedItem merged = mergeChangedItems(serverItem, mergeItem);

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
        copyMergedIntoServerItem(mergeItem, serverItem);
        serverItem.setChanged(true);

        return serverItem;
    }

    private void copyMergedIntoServerItem(CollectedItem mergeItem, CollectedItem serverItem) {
        ItemEntity nakedServer = serverItem.getItem();
        ItemEntity nakedClient = mergeItem.getItem();
        nakedServer.setAddedOn(nakedClient.getAddedOn());
        nakedServer.setCrossedOff(nakedClient.getCrossedOff());
        nakedServer.setFreeText(nakedClient.getFreeText());
        nakedServer.setFrequent(nakedClient.isFrequent());
        nakedServer.setRemovedOn(nakedClient.getRemovedOn());
        nakedServer.setUpdatedOn(nakedClient.getUpdatedOn());
        nakedServer.setUsedCount(nakedClient.getUsedCount());

        serverItem.setItem(nakedServer);
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
*/


}