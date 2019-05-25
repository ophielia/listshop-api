package com.meg.atable.lmt.data.repository.impl;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.repository.ItemChangeRepository;
import com.meg.atable.lmt.data.repository.ItemRepository;
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
    public void saveItemChanges(ItemCollector collector, Long userId) {
        if (collector instanceof ListItemCollector) {
            listTagStatisticService.processStatistics(userId, (ListItemCollector) collector);
        }

        List<ItemEntity> toUpdate = collector.getChangedItems();

        if (!toUpdate.isEmpty()) {
            itemRepository.saveAll(toUpdate);
        }
    }
}
