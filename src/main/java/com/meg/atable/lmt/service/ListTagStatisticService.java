package com.meg.atable.lmt.service;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ListTagStatisticService {


    // TODO this is a kludge for now - needs to be part of settings - or at least a type.
    String IS_FREQUENT = "InThePantry";

    void processStatistics(Long userId, ListItemCollector collector);

}
