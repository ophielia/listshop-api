package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ "dish_id","name", "description", "reference", "user_id","last_added", "tags", "ingredients", "ratings"  })
public class Dish {

    @JsonProperty("dish_id")
    private String dishId;

    @JsonProperty("name")
    private String dishName;

    private String description;

    private String reference;

    private List<NestedTag> tags = new ArrayList<>();

    private List<Ingredient> ingredients = new ArrayList<>();

    @JsonProperty("ratings")
    private List<RatingInfo> dishRatings = new ArrayList<>();

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("last_added")
    private Date lastAdded;

    public Dish() {
        // for jackson
    }

    public Dish(Long dishId) {
        this.dishId = String.valueOf(dishId);
    }

    public Dish withDishId(String dishId) {
        this.dishId = dishId;
        return this;
    }

    public Dish withDishName(String dishName) {
        this.dishName = dishName;
        return this;
    }

    public Dish withDescription(String description) {
        this.description = description;
        return this;
    }

    public Dish withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public Dish withTags(List<NestedTag> tags) {
        this.tags = tags;
        return this;
    }

    public Dish withIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public Dish withRatings(List<RatingInfo> dishRatings) {
        this.dishRatings = dishRatings;
        return this;
    }

    public Dish withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Dish withLastAdded(Date lastAdded) {
        this.lastAdded = lastAdded;
        return this;
    }

    public String getDishId() {
        return dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDescription() {
        return description;
    }

    public String getReference() {
        return reference;
    }

    public List<NestedTag> getTags() {
        return tags;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<RatingInfo> getRatings() {
        return dishRatings;
    }

    public String getUserId() {
        return userId;
    }

    public Date getLastAdded() {
        return lastAdded;
    }
}
