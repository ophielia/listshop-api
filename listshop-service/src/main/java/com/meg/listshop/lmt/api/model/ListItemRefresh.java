package com.meg.listshop.lmt.api.model;

import com.meg.listshop.lmt.service.categories.ListShopCategory;

public class ListItemRefresh {

    private Item item;

    private ListShopCategory category;

    public ListItemRefresh(Item item, ListShopCategory category) {
        this.item = item;
        this.category = category;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ListShopCategory getCategory() {
        return category;
    }

    public void setCategory(ListShopCategory category) {
        this.category = category;
    }
}
