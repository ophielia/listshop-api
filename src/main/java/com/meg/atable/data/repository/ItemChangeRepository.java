package com.meg.atable.data.repository;

import com.meg.atable.service.ListItemCollector;
import org.springframework.stereotype.Component;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void saveItemChanges(ListItemCollector collector, Long userId);
}
