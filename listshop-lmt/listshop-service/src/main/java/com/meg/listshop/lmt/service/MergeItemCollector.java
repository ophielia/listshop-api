package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ListItemEntity;

import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class MergeItemCollector extends AbstractItemCollector {

    private Date listLastUpdate;

    public MergeItemCollector(Long listId, List<ListItemEntity> items, Date listLastUpdate) {
        super(listId, items);
        this.listLastUpdate = listLastUpdate;
    }

    public MergeItemCollector(Long listId) {
        super(listId);
    }

    // merge collector
    public void addMergeItems(List<ListItemEntity> mergeItems) {
        if (mergeItems == null || mergeItems.isEmpty()) {
            return;
        }
        // go through all merge items
        Iterator<ListItemEntity> mergeIterator = mergeItems.iterator();
        while (mergeIterator.hasNext()) {
            ListItemEntity mergeListItemEntity = mergeIterator.next();
            Long tagId = mergeListItemEntity.getTag().getId();

            if (getTagCollectedMap().containsKey(tagId)) {
                CollectedItem serverItem = getTagCollectedMap().get(mergeListItemEntity.getTag().getId());
                CollectedItem mergeItem = new CollectedItem(mergeListItemEntity);
                // if merge item matches an item in the tagCollectedItems
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
        for (ListItemEntity newMergeItem : mergeItems ) {
            // create new collected item
            CollectedItem item = new CollectedItem(newMergeItem);

            // skip items created before server list was last updated
            if (item.createdBefore(listLastUpdate)) {
                continue;
            }

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
