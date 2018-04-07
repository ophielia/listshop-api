package com.meg.atable.service;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ListTagStatisticService {


    // MM this is a kludge for now - needs to be part of settings - or at least a type.
    public final static String IS_FREQUENT = "InThePantry";

    void processStatistics(Long userId, ListItemCollector collector);

}
