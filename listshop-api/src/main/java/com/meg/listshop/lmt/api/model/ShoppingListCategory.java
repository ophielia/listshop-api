package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListCategory {

    String name;

    @JsonProperty("category_id")
    private Long id;

    @JsonProperty("displayOrder")
    int displayOrder;

    @JsonProperty("subcategories")
    private List<ShoppingListCategory> subCategories = new ArrayList<>();

    @JsonProperty("items")
    private List<Item> items;

    public ShoppingListCategory() {
    }

    public ShoppingListCategory(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<ShoppingListCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<ShoppingListCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}