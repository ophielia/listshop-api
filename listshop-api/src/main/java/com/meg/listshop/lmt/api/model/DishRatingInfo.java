package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DishRatingInfo {

    private Long dishId;

    private String dishName;

    private Set<RatingInfo> ratings;

    public DishRatingInfo() {
    }

    public DishRatingInfo(Long id, String dishName) {
        this.dishId = id;
        this.dishName = dishName;
    }

    @JsonProperty("dish_id")
    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    @JsonProperty("dish_name")
    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    @JsonProperty("ratings")
    public Set<RatingInfo> getRatings() {
        return ratings;
    }

    public void setRatings(Set<RatingInfo> ratings) {
        this.ratings = ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishRatingInfo that = (DishRatingInfo) o;
        return dishId.equals(that.dishId) &&
                dishName.equals(that.dishName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId, dishName);
    }

    public void addRating(RatingInfo value) {
        if (ratings == null) {
            ratings = new HashSet<>();
        }
        ratings.add(value);
    }
}
