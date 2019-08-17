package com.meg.atable.lmt.api.model;

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
