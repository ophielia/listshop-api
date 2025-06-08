package com.meg.listshop.lmt.list;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.AbstractItemCollector;
import com.meg.listshop.lmt.service.CollectedItem;
import com.meg.listshop.lmt.service.CollectorContext;
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

    public ListItemCollector(Long listId, List<ListItemEntity> items) {
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

    public void copyExistingItemsIntoList(List<ListItemEntity> items, CollectorContext context) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> copyOrUpdateExistingItem(item, context));
    }

    private void copyOrUpdateExistingItem(ListItemEntity item, CollectorContext context) {
        // do not copy crossed off items
        if ((item.getCrossedOff() != null && !context.isCopyCrossedOff()) || item.getRemovedOn() != null) {
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

        update.add(item, DateUtils.asLocalDateTime(item.getCrossedOff()), context, isNew);
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

    public void removeFreeTextItem(ListItemEntity listItemEntity) {
        // TODO implement this

    }


    private CollectedItem copyFreeTextItem(ListItemEntity item) {
        ListItemEntity copiedItem = item.createCopy();

        CollectedItem copied = new CollectedItem(copiedItem);
        copied.setFreeText(item.getFreeText());
        return copied;
    }

    public void removeItemsFromList(List<ListItemEntity> fromListItems, CollectorContext context) {
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
            ListItemEntity item = tagCollectedEntry.getValue().getItem();
            String entryListSource = item.getRawListSources();
            boolean listSourceMatch = entryListSource.contains(String.valueOf(fromListId));
            if (fromListMatch || listSourceMatch) {
                tagCollectedEntry.getValue().remove(context);
                getTagCollectedMap().put(item.getTag().getId(), tagCollectedEntry.getValue());
            }
        }

    }

    public void removeTagsForDish(Long dishId, List<TagEntity> tagsToRemove, CollectorContext context) {

        for (TagEntity tag : tagsToRemove) {
            removeItemByTagId(tag.getId(), context);
        }
    }





}
