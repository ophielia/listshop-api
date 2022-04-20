package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class Category {

    @JsonProperty("category_id")
    private Long id;

    private String name;

    @Deprecated
    @JsonProperty("displayOrder")
    private Integer depDisplayOrder;

    @JsonProperty("display_order")
    private Integer displayOrder;

    private java.util.List<Item> items = new ArrayList<>();

    private java.util.List<Tag> tags = new ArrayList<>();

    @JsonProperty("category_type")
    private String categoryType;

    @Deprecated
    @JsonProperty("subcategories")
    private List<Category> subCategories = new ArrayList<>();


    public Category() {
        // empty constructor
    }

    public Category(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Category name(String name) {
        this.name = name;
        return this;
    }

    public Integer getDepDisplayOrder() {
        return depDisplayOrder;
    }

    public void setDepDisplayOrder(Integer depDisplayOrder) {
        this.depDisplayOrder = depDisplayOrder;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Category displayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        this.setDepDisplayOrder(this.displayOrder);
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public Category items(List<Item> items) {
        this.items = items;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Category tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public Category categoryType(String categoryType) {
        this.categoryType = categoryType;
        return this;
    }

    @Override
    public String toString() {
        return "NewCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", depDisplayOrder=" + depDisplayOrder +
                ", displayOrder=" + displayOrder +
                ", items=" + items +
                ", tags=" + tags +
                ", categoryType='" + categoryType + '\'' +
                '}';
    }
}
