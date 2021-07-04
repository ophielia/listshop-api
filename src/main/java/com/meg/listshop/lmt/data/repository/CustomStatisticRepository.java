package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.service.impl.StatisticOperationType;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface CustomStatisticRepository {

    List<Long> getTagIdsForMissingStats(Long userId, Iterable<Long> tagIds);

    List<Long> getFrequentTagIds(Long userId, Long listId);

    void insertSingleUserStatistic(Long userId, Long tagId, Integer addedSingle, Integer removedSingle);

    void insertEmptyUserStatistics(Long userId, List<Long> tagIds);

    void updateUserStatistics(Long userId, List<Long> updateIds, StatisticOperationType operation, StatisticCountType countType);
}
