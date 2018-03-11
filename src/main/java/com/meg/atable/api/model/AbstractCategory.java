package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public abstract class AbstractCategory implements Category {

    @JsonProperty("category_id")
    private Long id;

    private String name;

    private List<Category> subCategories = new ArrayList<>();

    private Integer displayOrder;

    public AbstractCategory(String name) {
        this.name = name;
    }

    public AbstractCategory(String name, Long id) {
        this.name = name;
        this.id = id;
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
    public Category name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }


    @Override
    public List<Category> getSubCategories() {
        return subCategories;
    }

    @Override
    public Category subCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
        return this;
    }

    @Override
    public void addSubCategory(Category subcategory) {
        this.subCategories.add(subcategory);
    }


    @Override
    public int getDisplayOrder() {
        return displayOrder!=null?displayOrder:0;
    }

    @Override
    public Category displayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }


}
