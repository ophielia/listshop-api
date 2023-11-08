package com.meg.listshop.lmt.service.categories;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public abstract class AbstractCategory implements ListShopCategory {

    @JsonProperty("category_id")
    private Long id;

    private String name;

    @JsonProperty("subcategories")
    private List<ListShopCategory> subCategories = new ArrayList<>();


    private Integer displayOrder;

    public AbstractCategory(String name) {
        this.name = name;
    }

    public AbstractCategory(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public AbstractCategory(Long id, String name, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    protected AbstractCategory() {
    }

    public AbstractCategory(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ListShopCategory name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }


    @Override
    public List<ListShopCategory> getSubCategories() {
        return subCategories;
    }

    @Override
    public ListShopCategory subCategories(List<ListShopCategory> subCategories) {
        this.subCategories = subCategories;
        return this;
    }

    @Override
    public void addSubCategory(ListShopCategory subcategory) {
        this.subCategories.add(subcategory);
    }


    @Override
    public int getDisplayOrder() {
        return displayOrder!=null?displayOrder:0;
    }

    @Override
    public ListShopCategory displayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }


}
