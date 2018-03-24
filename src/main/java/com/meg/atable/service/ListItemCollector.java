package com.meg.atable.service;

import com.meg.atable.api.model.ItemSourceType;
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
    private Map<Long, Long> categoryDictionary;
    private Map<Long, ItemEntity> tagToItem;
    private List<ItemEntity> freeTextItems;

    // MM come back and clean up

    public ListItemCollector(Long savedNewListId, Map<Long, Long> categoryDictionary) {
        this.listId = savedNewListId;
        this.categoryDictionary = categoryDictionary;
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }

    public ListItemCollector(Long savedNewListId, List<ItemEntity> items) {
        this.listId = savedNewListId;
        this.categoryDictionary = new HashMap<>();
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
        addItems(items);
    }

    public ListItemCollector(Long listId) {
        this.listId = listId;
        this.categoryDictionary = new HashMap<>();
        tagToItem = new HashMap<>();
        freeTextItems = new ArrayList<>();
    }

    public void addTags(List<TagEntity> tagEntityList, Long dishId) {
        for (TagEntity tag : tagEntityList) {
            if (tagToItem.containsKey(tag.getId())) {
                addTagToItem(tag.getId(), dishId);
            } else {
                createItemFromTag(tag, dishId);
            }
        }
    }

    public void addItems(List<ItemEntity> items) {
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

    public List<TagEntity> getTagsByCategories(List<String> categories) {
        return tagToItem.values().stream()
                .filter(i -> i.getTag() != null && i.getListCategory() == null ||
                        i.getTag() != null && categories.contains(i.getListCategory()))
                .map(ItemEntity::getTag)
                .collect(Collectors.toList());
    }

    public List<Long> getAllTagIds() {
        return tagToItem.values().stream()
                .map(i -> i.getTag().getId())
                .collect(Collectors.toList());
    }

    public void categorizeUncategorized(Map<Long, Long> dictionary) {
        dictionary.entrySet().stream()
                .forEach(e -> {
                    ItemEntity item = tagToItem.get(e.getKey());
                    //item.setCategoryId(e.getValue());
                    tagToItem.put(e.getKey(), item);
                });
    }

    public void copyExistingItemsIntoList(ItemSourceType sourceType, List<ItemEntity> items) {
        if (items == null) {
            return;
        }
        items.stream().forEach(item -> addOrUpdateItem(item, sourceType));
    }

    private void addOrUpdateItem(ItemEntity item, ItemSourceType sourceType) {
        if (item.getTag() == null) {
            ItemEntity copied = copyItem(item);
            // free text item
            freeTextItems.add(copied);
        } else if (tagToItem.containsKey(item.getTag().getId())) {
            ItemEntity update = tagToItem.get(item.getTag().getId());
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            update.setUsedCount(count + 1);
            update.addRawItemSource(sourceType.name());
            tagToItem.put(item.getTag().getId(), update);
        } else {
            ItemEntity copied = copyItem(item);
            int count = item.getUsedCount() != null ? item.getUsedCount() : 0;
            copied.setUsedCount(count + 1);
            copied.addRawItemSource(sourceType.name());
            tagToItem.put(item.getTag().getId(), copied);
        }
    }

    private void createItemFromTag(TagEntity tag, Long dishId) {
        ItemEntity item = new ItemEntity();
        item.setTag(tag);
        item.setListId(listId);
        item.addRawDishSource(dishId);
        item.setUsedCount(1);
        tagToItem.put(tag.getId(), item);
    }

    private void addTagToItem(Long tagid, Long dishId) {
        ItemEntity item = tagToItem.get(tagid);
        item.setUsedCount(item.getUsedCount() + 1);
        item.addRawDishSource(dishId);
        tagToItem.put(tagid, item);
    }


    private ItemEntity copyItem(ItemEntity item) {
        ItemEntity copied = new ItemEntity();
        copied.setUsedCount(0); // MM resetting count when adding from another list
        copied.setTag(item.getTag());
        copied.setListId(listId);
        copied.setFreeText(item.getFreeText());
        copied.setAddedOn(new Date()); // MM also need to think about this
        return copied;
    }


}
