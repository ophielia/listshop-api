package com.meg.listshop.lmt.list;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.ListOperationType;
import com.meg.listshop.lmt.api.model.Statistic;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ListTagStatisticService {


    void countTagAddedToDish(Long userId, Long tagId);

    void legacyProcessCollectorStatistics(Long userId, ItemCollector collector, CollectorContext context);

    void processStatistics(Long userId, List<ListItemEntity> items, List<Long> removedTagIds, ListOperationType operationType);

    List<ListTagStatistic> getStatisticsForUser(Long id, int resultLimit);

    void createStatisticsForUser(UserEntity user, List<Statistic> statisticList);

    List<Long> findFrequentIdsForList(Long listId, Long userId);
}
