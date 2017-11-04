package com.meg.atable.api.model;

import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public class Category {

    private String name;

    private java.util.List<Item> items;


    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Category name(String name) {
        this.name = name;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public Category items(List<Item> items) {
        this.items = items;
        return this;
    }
}
