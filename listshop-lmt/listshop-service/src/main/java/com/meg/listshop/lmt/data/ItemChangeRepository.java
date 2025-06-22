package com.meg.listshop.lmt.data;

import com.meg.listshop.lmt.api.model.ListOperationType;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;

import java.util.List;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void legacySaveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context);

    void saveItemChangeStatistics(ShoppingListEntity shoppingList, List<ListItemEntity> items, Long userId, ListOperationType operationType);
}
