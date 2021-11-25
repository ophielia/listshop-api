package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Created by margaretmartin on 12/04/2018.
 */
public class RatingUpdateInfo {

    private Set<RatingInfo> ratingHeaders;

    private Set<DishRatingInfo> dishRatingInfoSet;

    public RatingUpdateInfo(Set<RatingInfo> headers, Set<DishRatingInfo> dishRatingInfoSet) {
        this.ratingHeaders = headers;
        this.dishRatingInfoSet = dishRatingInfoSet;
    }


    @JsonProperty("headers")
    public Set<RatingInfo> getRatingHeaders() {
        return ratingHeaders;
    }

    public void setRatingHeaders(Set<RatingInfo> ratingHeaders) {
        this.ratingHeaders = ratingHeaders;
    }

    @JsonProperty("dish_ratings")
    public Set<DishRatingInfo> getDishRatingInfoSet() {
        return dishRatingInfoSet;
    }

    public void setDishRatingInfoSet(Set<DishRatingInfo> dishRatingInfoSet) {
        this.dishRatingInfoSet = dishRatingInfoSet;
    }
}
