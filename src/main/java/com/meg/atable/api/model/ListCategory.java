package com.meg.atable.api.model;

import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ListCategory {

    private String categoryId;

    private String name;

    private List<Item> items;

    public ListCategory(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public ListCategory categoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ListCategory name(String name) {
        this.name = name;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public ListCategory items(List<Item> items) {
        this.items = items;
        return this;
    }
}
