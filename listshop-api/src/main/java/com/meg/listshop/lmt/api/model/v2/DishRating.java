package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.RatingInfo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DishRating {

    private NestedDish dish;
    private Set<RatingInfo> ratings;


    public DishRating() {
    }
    public DishRating(com.meg.listshop.lmt.api.model.DishRatingInfo ratingInfo) {
       //MM start here
    }

    public DishRating withDish(NestedDish dish) {
        this.dish = dish;
        return this;
    }

    public DishRating withRatings(Set<RatingInfo> ratings) {
        this.ratings = ratings;
        return this;
    }

    public NestedDish getDish() {
        return dish;
    }

    public Set<RatingInfo> getRatings() {
        return ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishRating that = (DishRating) o;
        return Objects.equals(dish, that.dish) && Objects.equals(ratings, that.ratings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dish, ratings);
    }

    @Override
    public String toString() {
        return "DishRating{" +
                "dish=" + dish +
                ", ratings=" + ratings +
                '}';
    }
}
