package com.meg.listshop.lmt.api.model;

public class ListItemRefresh {

    private Item item;

    private Category category;

    public ListItemRefresh(Item item, Category category) {
        this.item = item;
        this.category = category;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
