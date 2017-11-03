package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dish {

    private Long dish_id;

    private String dishName;

    private String description;

    private List<Tag> tags = new ArrayList<>();

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
        this.dish_id = id;
    }

    @JsonProperty("dish_id")
    public Long getId() {
        return dish_id;
    }

    @JsonProperty("name")
    public String getDishName() {
        return dishName;
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

    public Date getLastAdded() {
        return lastAdded;
    }

    public Dish lastAdded(Date date) {
            this.lastAdded = date;
            return this;
    }
}