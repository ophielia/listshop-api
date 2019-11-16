package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ContextType;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void addTags(List<TagEntity> tagEntityList, CollectorContext context) {
        for (TagEntity tag : tagEntityList) {
            addItemByTag(tag, context);
        }
    }

    public void copyExistingItemsIntoList(Long fromListId, List<ItemEntity> items, boolean incrementStats) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> copyOrUpdateExistingItem(item, fromListId, incrementStats));
    }

    private void copyOrUpdateExistingItem(ItemEntity item, Long fromListId, boolean incrementStats) {
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
            update.addRawListSource(String.valueOf(fromListId));
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
            copied.addRawListSource(String.valueOf(fromListId));
            copied.setRawDishSources(item.getRawDishSources());
            if (incrementStats) {
                copied.incrementAddCount(Math.max(1, item.getUsedCount()));
            }
            getTagCollectedMap().put(item.getTag().getId(), copied);
        }
    }

    private CollectedItem copyItem(ItemEntity item) {
        //MM start here
        ItemEntity copiedItem = item.clone();

        CollectedItem copied = new CollectedItem(copiedItem);
        copied.setFreeText(item.getFreeText());
        return copied;
    }


    public void removeItemByTagId(Long tagId, CollectorContext context) {
        if (!getTagCollectedMap().containsKey(tagId)) {
            return;
        }
        CollectedItem update = getTagCollectedMap().get(tagId);

        update.remove(context);

        getTagCollectedMap().put(tagId, update);
    }

    public void removeFreeTextItem(ItemEntity itemEntity) {
        // TODO implement this
    }

    public void removeItemsFromList(List<ItemEntity> fromListItems, CollectorContext context) {
        Long fromListId = context.getListId();
        Set<Long> fromTagIds = new HashSet<>();
        if (fromListItems != null) {
            fromTagIds = fromListItems.stream().map(itemEntity -> itemEntity.getTag().getId()).collect(Collectors.toSet());
        }
        // go through all collected items
        // check for existence in fromList items
        // check for listSource in item
        // if either, remove

        for (Map.Entry<Long, CollectedItem> tagCollectedEntry : getTagCollectedMap().entrySet()) {
            boolean fromListMatch = fromTagIds.contains(tagCollectedEntry.getKey());
            ItemEntity item = tagCollectedEntry.getValue().getItem();
            String entryListSource = item.getRawListSources();
            boolean listSourceMatch = entryListSource.contains(String.valueOf(fromListId));
            if (fromListMatch || listSourceMatch) {
                tagCollectedEntry.getValue().remove(context);
                getTagCollectedMap().put(item.getTag().getId(), tagCollectedEntry.getValue());
            }
        }

    }

    public void removeTagsForDish(Long dishId, List<TagEntity> tagsToRemove) {
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .withDishId(dishId)
                .withRemoveEntireItem(false)
                .withIncrementStatistics(false)
                .build();

        for (TagEntity tag : tagsToRemove) {
            removeItemByTagId(tag.getId(), context);
        }
    }





}
