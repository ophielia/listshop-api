package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.Statistic;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import com.meg.listshop.lmt.data.entity.StatisticOperationType;
import com.meg.listshop.lmt.data.repository.ListTagStatisticRepository;
import com.meg.listshop.lmt.service.CollectedItem;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;
import com.meg.listshop.lmt.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
@Transactional
public class ListTagStatisticServiceImpl implements ListTagStatisticService {

    private ListTagStatisticRepository listTagStatisticRepo;

    @Autowired
    public ListTagStatisticServiceImpl(ListTagStatisticRepository listTagStatisticRepo) {
        this.listTagStatisticRepo = listTagStatisticRepo;
    }

    @Override
    public void countTagAddedToDish(Long userId, Long tagId) {
        var statistic = listTagStatisticRepo.findByUserIdAndTagId(userId, tagId);

        if (statistic == null) {
            statistic = new ListTagStatistic();
            statistic.setTagId(tagId);
            statistic.setUserId(userId);
        }
        int addedCount = statistic.getAddedToDishCount() != null ? statistic.getAddedToDishCount() : 0;
        statistic.setAddedToDishCount(addedCount + 1);
        listTagStatisticRepo.save(statistic);
    }

    @Override
    public void processCollectorStatistics(Long userId, ItemCollector collector, CollectorContext context) {
        // pull tagIds for removed and created tags
        List<Long> removedIds = new ArrayList<>();
        List<Long> addedIds = new ArrayList<>();
        collector.getCollectedTagItems().stream()
                .filter(CollectedItem::isChanged)
                .forEach(item -> {
                    if (item.isAdded() || item.isCountAdded()) {
                        addedIds.add(item.getTagId());
                    } else if ((item.isRemoved() || item.isCountDecreased()) && item.getCrossedOff() == null) {
                        removedIds.add(item.getTagId());
                    }
                });

        if (removedIds.isEmpty() && addedIds.isEmpty()) {
            return;
        }

        // check for and create missing statistics
        checkForAndCreateMissingStatistics(collector.getAllTagIds(), userId);

        // update removed
        if (!removedIds.isEmpty()) {
            listTagStatisticRepo.updateUserStatistics(userId, removedIds, StatisticOperationType.remove, context.getStatisticCountType());
        }
        // update added
        if (!addedIds.isEmpty()) {
            listTagStatisticRepo.updateUserStatistics(userId, addedIds, StatisticOperationType.add, context.getStatisticCountType());
        }
    }

    @Override
    public List<ListTagStatistic> getStatisticsForUser(Long userId, int resultLimit) {

        List<ListTagStatistic> statistics = listTagStatisticRepo.findByUserId(userId);
        if (statistics.size() > resultLimit) {
            return statistics.subList(0, resultLimit);
        }
        return statistics;
    }

    @Override
    public void createStatisticsForUser(UserEntity user, List<Statistic> statisticList) {
        // this is done from a context in which the user has just been created, and doesn't
        // have any statistics
        if (statisticList.isEmpty()) {
            return;
        }

        // put statistic objects into hash
        Map<Long, Statistic> statMap = statisticList.stream()
                .collect(Collectors.toMap(Statistic::getTagId, Function.identity()));

        List<Long> idsToInsert = listTagStatisticRepo.getTagIdsForMissingStats(user.getId(), statMap.keySet());

        // return if nothing to insert
        if (idsToInsert == null) {
            return;
        }

        // insert all statistics
        for (Long insertId : idsToInsert) {
            Statistic insertStat = statMap.get(insertId);
            insertUserStatistic(insertId, user.getId(), insertStat.getAddedCount(), insertStat.getRemovedCount());
        }

    }

    @Override
    public List<Long> findFrequentIdsForList(Long listId, Long userId) {
        return listTagStatisticRepo.getFrequentTagIds(userId, listId);
    }

    private void insertUserStatistic(Long tagId, Long userId, Integer addedCount, Integer removedCount) {
        listTagStatisticRepo.insertSingleUserStatistic(userId, tagId, addedCount, removedCount);
    }

    private void checkForAndCreateMissingStatistics(List<Long> tagIds, Long userId) {


        List<Long> missingIds = listTagStatisticRepo.getTagIdsForMissingStats(userId, tagIds);

        if (missingIds.isEmpty()) {
            return;
        }

        listTagStatisticRepo.insertEmptyUserStatistics(userId, missingIds);
    }
}
