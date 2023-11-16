package com.meg.listshop.lmt.service.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.CategoryType;
import com.meg.listshop.lmt.api.model.Item;
import com.meg.listshop.lmt.data.entity.ListItemEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public class ItemCategoryPojo extends AbstractCategory {

    private java.util.List<Item> items = new ArrayList<>();

    private String categoryType;


    @JsonIgnore
    List<ListItemEntity> itemEntities = new ArrayList<>();

    public ItemCategoryPojo() {
    }

    public ItemCategoryPojo(String name) {
        super(name);
    }

    public ItemCategoryPojo(String name, Long id, CategoryType categoryType) {
        super(name, id);
        this.categoryType = categoryType.name();
    }


    public ItemCategoryPojo(String name, Long id, Integer displayOrder, CategoryType categoryType) {
        super(id, name, displayOrder);
        this.categoryType = categoryType.name();
    }

    public List<Item> getItems() {
        return items;
    }

    public ListShopCategory items(List<Item> items) {
        this.items = items;
        return this;
    }

    public void addItemEntity(ListItemEntity item) {
        this.itemEntities.add(item);
    }

    public List<ListItemEntity> getItemEntities() {
        return itemEntities;
    }

    public ListShopCategory itemEntities(List<ListItemEntity> itemEntities) {
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

    @JsonProperty("tags")
    public List<String> getTags() {
        // client requires tags - empty in this implementation
        return new ArrayList<>();

    }

    @Override
    public boolean isEmpty() {
        return getSubCategories().isEmpty() &&
                getItems().isEmpty() &&
                getItemEntities().isEmpty();
    }

    public void sortItems() {
        this.itemEntities.sort(Comparator.comparing(ListItemEntity::getDisplay));
    }
}
