package com.meg.atable.data.repository;

import com.meg.atable.service.ListItemCollector;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void saveItemChanges(ListItemCollector collector, Long userId);
}
