package com.meg.atable.lmt.data.repository.impl;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import com.meg.atable.lmt.data.repository.ItemChangeRepository;
import com.meg.atable.lmt.data.repository.ItemRepository;
import com.meg.atable.lmt.service.CollectorContext;
import com.meg.atable.lmt.service.ItemCollector;
import com.meg.atable.lmt.service.ListItemCollector;
import com.meg.atable.lmt.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by margaretmartin on 05/04/2018.
 */
@Component
public class ListItemRepositoryImpl implements ItemChangeRepository {

    @Autowired
    private ListTagStatisticService listTagStatisticService;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void saveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context) {
        if (collector instanceof ListItemCollector && context.isIncrementStatistics()) {
            listTagStatisticService.processCollectorStatistics(userId, (ListItemCollector) collector);
        }

        List<ItemEntity> toUpdate = collector.getChangedItems();
        toUpdate.stream().forEach(item -> item.setListId(shoppingList.getId()));

        if (!toUpdate.isEmpty()) {
            itemRepository.saveAll(toUpdate);
        }
    }
}
