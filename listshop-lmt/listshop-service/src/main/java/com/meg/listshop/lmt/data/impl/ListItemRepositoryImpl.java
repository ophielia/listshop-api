package com.meg.listshop.lmt.data.impl;

import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
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

    private final ListTagStatisticService listTagStatisticService;

    private final ItemRepository itemRepository;

    @Autowired
    public ListItemRepositoryImpl(ListTagStatisticService listTagStatisticService,
                                  ItemRepository itemRepository) {
        this.listTagStatisticService = listTagStatisticService;
        this.itemRepository = itemRepository;
    }

    @Override
    public void saveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context) {
        if (context.getStatisticCountType() != StatisticCountType.None) {
            listTagStatisticService.processCollectorStatistics(userId, collector, context);
        }

        List<ListItemEntity> toUpdate = collector.getChangedItems();
        toUpdate.stream().forEach(item -> item.setListId(shoppingList.getId()));

        if (!toUpdate.isEmpty()) {
            itemRepository.saveAll(toUpdate);
        }
    }
}
