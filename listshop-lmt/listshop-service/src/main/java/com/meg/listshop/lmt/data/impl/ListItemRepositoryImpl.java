/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.impl;

import com.meg.listshop.lmt.api.model.ListOperationType;
import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 05/04/2018.
 */
@Component
public class ListItemRepositoryImpl implements ItemChangeRepository {

    private final ListTagStatisticService listTagStatisticService;

    private final ItemRepository itemRepository;
    private final ListItemDetailRepository itemDetailRepository;

    @Autowired
    public ListItemRepositoryImpl(ListTagStatisticService listTagStatisticService,
                                  ItemRepository itemRepository,
                                  ListItemDetailRepository itemDetailRepository) {
        this.listTagStatisticService = listTagStatisticService;
        this.itemRepository = itemRepository;
        this.itemDetailRepository = itemDetailRepository;
    }

    @Override
    public void legacySaveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context) {
        if (context.getStatisticCountType() != StatisticCountType.None) {
            listTagStatisticService.legacyProcessCollectorStatistics(userId, collector, context);
        }

        List<ListItemEntity> toUpdate = collector.getChangedItems();
        Date updateDate = new Date();
        toUpdate.stream().forEach(item -> {
            item.setListId(shoppingList.getId());
            item.setLastChanged(updateDate);
            item.setUpdatedOn(updateDate);
            if (item.getId() == null) {
                saveNewItemFromMerge(item);
            }
        });

        if (!toUpdate.isEmpty()) {
            itemRepository.saveAll(toUpdate);
        }
    }

    private ListItemEntity saveNewItemFromMerge(ListItemEntity item) {
        item = itemRepository.save(item);
        ListItemDetailEntity detailEntity = new ListItemDetailEntity();
        detailEntity.setItem(item);
        detailEntity.setCount(1);
        detailEntity.setLinkedListId(item.getListId());
        detailEntity = itemDetailRepository.save(detailEntity);
        item.addDetailToItem(detailEntity);
        return item;
    }


    @Override
    public void saveItemChangeStatistics(ShoppingListEntity shoppingList, List<ListItemEntity> items, List<Long> removedTagIds,Long userId, ListOperationType operationType) {
        if (ListOperationType.NONE != operationType) {
            listTagStatisticService.processStatistics(userId, items,removedTagIds, operationType);
        }
    }

}
