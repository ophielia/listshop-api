package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.data.entity.ItemEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public class ItemCategory extends AbstractCategory {

    private java.util.List<Item> items = new ArrayList<>();

    private String categoryType;

    @JsonIgnore
    List<ItemEntity> itemEntities = new ArrayList<>();


    public ItemCategory(String name) {
        super(name);
    }

    public ItemCategory(String name, Long id, CategoryType categoryType) {
        super(name, id);
        this.categoryType = categoryType.name();
    }

    public List<Item> getItems() {
        return items;
    }

    public Category items(List<Item> items) {
        this.items = items;
        return this;
    }

    public void addItemEntity(ItemEntity item) {
        this.itemEntities.add(item);
    }

    public List<ItemEntity> getItemEntities() {
        return itemEntities;
    }

    public Category itemEntities(List<ItemEntity> itemEntities) {
        this.itemEntities = itemEntities;
        return this;
    }

    @JsonProperty("category_type")
    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public boolean isEmpty() {
        if (getSubCategories().isEmpty() &&
                getItems().isEmpty() &&
                getItemEntities().isEmpty()) {
            return true;
        }
        return false;
    }

    public void sortItems() {
        this.itemEntities.sort(Comparator.comparing(ItemEntity::getDisplay));
    }
}
