package com.meg.listshop.lmt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultDish {

    @JsonProperty("dish_id")
    private Long dish_id;

    private String dishName;

    private String description;

    private String reference;

    private List<Tag> tags = new ArrayList<>();

    private Long userId;

    @JsonProperty("last_added")
    private Date lastAdded;

    public ResultDish(Long userId, String dishName) {
        this.userId = userId;
        this.dishName = dishName;
    }

    public ResultDish() {
        // empty constructor
    }

    public ResultDish(Long userId, String dishName, String description) {
        this.userId = userId;
        this.dishName = dishName;
        this.description = description;
    }

    public ResultDish(Long id) {
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

    @JsonProperty("name")
    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public ResultDish dishName(String dishName) {
        this.dishName = dishName;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public ResultDish tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    @SuppressWarnings("unused")
    public Long getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public ResultDish userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ResultDish description(String description) {
        this.description = description;
        return this;
    }

    @SuppressWarnings("unused")
    public Date getLastAdded() {
        return lastAdded;
    }

    public ResultDish lastAdded(Date date) {
        this.lastAdded = date;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public ResultDish reference(String reference) {
        this.reference = reference;
        return this;
    }
}