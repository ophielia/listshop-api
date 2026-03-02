package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingInfo;

import java.util.Set;

/**
 * Created by margaretmartin on 12/04/2018.
 */
public class Ratings {

    @JsonProperty("headers")
    private Set<RatingInfo> ratingHeaders;

    @JsonProperty("dish_ratings")
    private Set<RatingInfo> dishRatingInfoSet;

    public Ratings() {
    }

    public Ratings(Set<RatingInfo> ratingHeaders, Set<RatingInfo> dishRatingInfoSet) {
        this.ratingHeaders = ratingHeaders;
        this.dishRatingInfoSet = dishRatingInfoSet;
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "ratingHeaders=" + ratingHeaders +
                ", dishRatingInfoSet=" + dishRatingInfoSet +
                '}';
    }
}
