/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.model;

import java.util.Comparator;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ShoppingListItemComparator implements Comparator<ShoppingListItem> {
    @Override
    public int compare(ShoppingListItem item1, ShoppingListItem item2) {

        String name = item1 == null ? null : item1.getTagName();
        String comparename = item2 == null ? null : item2.getTagName();
        if (name == null) {
            name = "";
        }
        if (comparename == null) {
            comparename = "";
        }
        return name.toLowerCase().compareTo(comparename.toLowerCase());

    }

}
