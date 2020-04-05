package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.repository.ItemChangeRepository;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;
import com.meg.listshop.lmt.service.ListTagStatisticService;
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
        if (context.getStatisticCountType() != StatisticCountType.None) {
            listTagStatisticService.processCollectorStatistics(userId, collector, context);
        }

        List<ItemEntity> toUpdate = collector.getChangedItems();
        toUpdate.stream().forEach(item -> item.setListId(shoppingList.getId()));

        if (!toUpdate.isEmpty()) {
            itemRepository.saveAll(toUpdate);
        }
    }
}
