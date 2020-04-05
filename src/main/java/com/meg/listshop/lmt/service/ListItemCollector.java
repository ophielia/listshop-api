package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.model.ContextType;
import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.apache.commons.lang3.tuple.Pair;

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

    public void copyExistingItemsIntoList(List<ItemEntity> items, CollectorContext context) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> copyOrUpdateExistingItem(item, context));
    }

    private void copyOrUpdateExistingItem(ItemEntity item, CollectorContext context) {
        // do not copy crossed off items
        if (item.getCrossedOff() != null || item.getRemovedOn() != null) {
            return;
        }
        if (item.getTag() == null) {
            CollectedItem copied = copyFreeTextItem(item);
            // free text item
            getFreeTextItemList().add(copied);
        }


        Pair<Boolean, CollectedItem> collectedItemPair = findOrCreateItemByTagId(item.getTag());
        CollectedItem update = collectedItemPair.getRight();
        boolean isNew = collectedItemPair.getLeft();

        update.add(item, context, isNew);
        getTagCollectedMap().put(item.getTag().getId(), update);

    }

    public void removeItemByTagId(Long tagId, CollectorContext context) {
        if (!getTagCollectedMap().containsKey(tagId)) {
            return;
        }
        CollectedItem update = getTagCollectedMap().get(tagId);

        update.remove(context);

        getTagCollectedMap().put(tagId, update);
    }

    public void removeItemsByTagIds(List<Long> tagIds, CollectorContext context) {
        for (Long id : tagIds) {
            removeItemByTagId(id, context);
        }
    }

    public void removeFreeTextItem(ItemEntity itemEntity) {
        // TODO implement this

    }


    private CollectedItem copyFreeTextItem(ItemEntity item) {
        ItemEntity copiedItem = item.clone();

        CollectedItem copied = new CollectedItem(copiedItem);
        copied.setFreeText(item.getFreeText());
        return copied;
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
                .withStatisticCountType(StatisticCountType.Dish)
                .build();

        for (TagEntity tag : tagsToRemove) {
            removeItemByTagId(tag.getId(), context);
        }
    }





}
