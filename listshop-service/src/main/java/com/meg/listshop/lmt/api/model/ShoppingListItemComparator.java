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

        //MM layout fill in
        String name = item1 this.tag.getName();
        String comparename = ((ShoppingListItem) o).tag != null ? ((ShoppingListItem) o).getTag().getName() : ((ShoppingListItem) o).getFreeText();
        if (name == null) {
            name = "";
        }
        if (comparename == null) {
            comparename = "";
        }
        return name.toLowerCase().compareTo(comparename.toLowerCase());

    }

}
