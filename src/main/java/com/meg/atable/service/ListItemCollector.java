package com.meg.atable.service;

import com.meg.atable.api.model.ItemSourceType;
import com.meg.atable.api.model.ListType;
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
    private final Map<Long, String> categoryDictionary;
    private final Map<Long, ItemEntity> tagToItem;
    private final List<ItemEntity> freeTextItems;

    public ListItemCollector(Long savedNewListId, Map<Long, String> categoryDictionary) {
        this.listId = savedNewListId;
        this.categoryDictionary = categoryDictionary;
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }

    public void addTags(List<TagEntity> tagEntityList) {
        for (TagEntity tag : tagEntityList) {
            if (tagToItem.containsKey(tag.getId())) {
                addTagToItem(tag.getId(), ItemSourceType.MealPlan);
            } else {
                createItemFromTag(tag, ItemSourceType.MealPlan);
            }
        }
    }

    public List<ItemEntity> getItems() {
        return Stream.concat(tagToItem.values().stream(), freeTextItems.stream())
                .collect(Collectors.toList());
    }

    public List<TagEntity> getUncategorizedTags() {
        return tagToItem.values().stream()
                .filter(i -> i.getTag() != null && i.getListCategory() == null)
                .map(ItemEntity::getTag)
                .collect(Collectors.toList());
    }

    public List<Long> getAllTagIds() {
        return tagToItem.values().stream()
                .map(i -> i.getTag().getId())
                .collect(Collectors.toList());
    }

    public void categorizeUncategorized(Map<Long, String> dictionary) {
        dictionary.entrySet().stream()
                .forEach(e -> {
                    ItemEntity item = tagToItem.get(e.getKey());
                    item.setListCategory(e.getValue());
                    tagToItem.put(e.getKey(), item);
                });
    }

    private void createItemFromTag(TagEntity tag, ItemSourceType sourceType) {
        ItemEntity item = new ItemEntity();
        item.setTag(tag);
        item.setListId(listId);
        item.addItemSource(sourceType);
        item.setUsedCount(1);
        item.setListCategory(categoryDictionary.get(tag.getId()));
        tagToItem.put(tag.getId(), item);
    }

    private void addTagToItem(Long tagid, ItemSourceType sourceType) {
        ItemEntity item = tagToItem.get(tagid);
        item.setUsedCount(item.getUsedCount() + 1);
        item.addItemSource(sourceType);
        tagToItem.put(tagid, item);
    }

    public void addListItems(ListType listType, ItemSourceType sourceType, List<ItemEntity> items) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> addOrUpdateItem(listType, sourceType, item));
    }

    private void addOrUpdateItem(ListType listType, ItemSourceType itemType, ItemEntity item) {
        if (item.getTag() == null) {
            ItemEntity copied = copyItem(item, listType);
            // free text item
            freeTextItems.add(copied);
        } else if (tagToItem.containsKey(item.getTag().getId())) {
            ItemEntity update = tagToItem.get(item.getTag().getId());
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            update.setUsedCount(count + 1);
            update.addItemSource(itemType);
            tagToItem.put(item.getTag().getId(), update);
        } else {
            ItemEntity copied = copyItem(item, listType);
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            copied.setUsedCount(count + 1);
            copied.addItemSource(itemType);
            tagToItem.put(item.getTag().getId(), copied);
        }
    }

    private ItemEntity copyItem(ItemEntity item, ListType listType) {
        ItemEntity copied = new ItemEntity();
        copied.setUsedCount(0); // MM resetting count when adding from another list
        copied.setListCategory(null);  // MM will need to revisit this - other list may have different layout
        copied.setTag(item.getTag());
        copied.setListId(listId);
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date()); // MM also need to think about this
        return copied;
    }

}
