package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ListTagStatistic;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ListTagStatisticService {


    // TODO this is a kludge for now - needs to be part of settings - or at least a type.
    String IS_FREQUENT = "InThePantry";

    void countTagAddedToDish(Long userId, Long tagId);

    void processCollectorStatistics(Long userId, ListItemCollector collector);

    List<ListTagStatistic> getStatisticsForUser(Long id);
}
