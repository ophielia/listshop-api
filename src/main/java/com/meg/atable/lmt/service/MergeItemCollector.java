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

            // set added date, and changed, and null out the id
            item.setIsAdded(true);
            item.setId(null);


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
        serverItem.mergeFrom(mergeItem);
    }








}
