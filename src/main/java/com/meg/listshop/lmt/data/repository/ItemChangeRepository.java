package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;

/**
 * Created by margaretmartin on 05/04/2018.
 */

public interface ItemChangeRepository {
    void saveItemChanges(ShoppingListEntity shoppingList, ItemCollector collector, Long userId, CollectorContext context);
}
