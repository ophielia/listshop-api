package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.service.ItemCollector;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void saveItemChanges(ItemCollector collector, Long userId);
}
