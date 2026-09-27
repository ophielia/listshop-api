/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.RatingInfo;

import java.util.Set;

/**
 * Created by margaretmartin on 12/04/2018.
 */
public class DishRatings {

    @JsonProperty("headers")
    private Set<RatingInfo> ratingHeaders;

    @JsonProperty("ratings")
    private Set<DishRating> ratingsInfo;

    public DishRatings() {
    }

    public DishRatings(Set<RatingInfo> ratingHeaders, Set<DishRating> ratingsInfo) {
        this.ratingHeaders = ratingHeaders;
        this.ratingsInfo = ratingsInfo;
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "ratingHeaders=" + ratingHeaders +
                ", dishRatingInfoSet=" + ratingsInfo +
                '}';
    }
}
