package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ListOperationType {
    TAG_ADD(true),
    TAG_REMOVE(false),
    DISH_ADD(true),
    DISH_REMOVE(false),
    LIST_ADD(true),
    LIST_REMOVE(false),
    STARTERLIST_ADD(true),
    STARTERLIST_REMOVE(false),
    NONE(true);

    private final boolean isAdd;


    ListOperationType(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean getIsAdd() {
        return isAdd;
    }
}
