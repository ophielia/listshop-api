/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.meg.listshop.lmt.api.model.MergeConflicts;

public class MergeResult {

    private ShoppingList shoppingList;

    private MergeConflicts mergeConflicts;

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public MergeConflicts getMergeConflicts() {
        return mergeConflicts;
    }

    public void setMergeConflicts(MergeConflicts mergeConflicts) {
        this.mergeConflicts = mergeConflicts;
    }
}
