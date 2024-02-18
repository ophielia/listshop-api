package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class Food {

    @JsonProperty("id")
    private String foodId;

    @JsonProperty("category_id")
    private String categoryId;

    private String name;

    public Food() {
        // for jackson - it likes the empty constructors
    }

    public String getId() {
        return foodId;
    }

    public void setId(String foodId) {
        this.foodId = foodId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
