package com.meg.listshop.lmt.list;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.ListOperationType;
import com.meg.listshop.lmt.api.model.Statistic;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ListTagStatisticService {


    // TODO this is a kludge for now - needs to be part of settings - or at least a type.
    String IS_FREQUENT = "InThePantry";

    void countTagAddedToDish(Long userId, Long tagId);

    void legacyProcessCollectorStatistics(Long userId, ItemCollector collector, CollectorContext context);

    void processStatistics(Long userId, List<ListItemEntity> items, ListOperationType operationType);

    List<ListTagStatistic> getStatisticsForUser(Long id, int resultLimit);

    void createStatisticsForUser(UserEntity user, List<Statistic> statisticList);

    List<Long> findFrequentIdsForList(Long listId, Long userId);
}
