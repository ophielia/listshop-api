package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.v2.IngredientPut;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dish {

    @JsonProperty("dish_id")
    private Long dishId;

    @JsonProperty("name")
    private String dishName;

    private String description;

    private String reference;

    private List<Tag> tags = new ArrayList<>();

    private List<IngredientPut> ingredients = new ArrayList<>();

    private Long userId;

    @JsonProperty("last_added")
    private Date lastAdded;

    public Dish(Long userId, String dishName) {
        this.userId = userId;
        this.dishName = dishName;
    }

    public Dish() {
        // empty constructor
    }

    public Dish(Long userId, String dishName, String description) {
        this.userId = userId;
        this.dishName = dishName;
        this.description = description;
    }

    public Dish(Long id) {
        this.dishId = id;
    }

    @JsonIgnore
    public Long getId() {
        return dishId;
    }


    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Dish dishName(String dishName) {
        this.dishName = dishName;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Dish tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    @SuppressWarnings("unused")
    public Long getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public Dish userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Dish description(String description) {
        this.description = description;
        return this;
    }

    @SuppressWarnings("unused")
    public Date getLastAdded() {
        return lastAdded;
    }

    public Dish lastAdded(Date date) {
        this.lastAdded = date;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Dish reference(String reference) {
        this.reference = reference;
        return this;
    }

    public List<IngredientPut> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientPut> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "dish_id=" + dishId +
                ", dishName='" + dishName + '\'' +
                ", description='" + description + '\'' +
                ", reference='" + reference + '\'' +
                ", tags=" + tags +
                ", ingredients=" + ingredients +
                ", userId=" + userId +
                ", lastAdded=" + lastAdded +
                '}';
    }
}