package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import com.meg.atable.lmt.service.CollectorContext;
import com.meg.atable.lmt.service.ItemCollector;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void saveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context);
}
