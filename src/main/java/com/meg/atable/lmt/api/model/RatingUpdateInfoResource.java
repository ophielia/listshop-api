package com.meg.atable.lmt.api.model;


import org.springframework.hateoas.ResourceSupport;

public class RatingUpdateInfoResource extends ResourceSupport {

    private final RatingUpdateInfo ratingUpdateInfo;

    public RatingUpdateInfoResource(RatingUpdateInfo ratingUpdateInfo) {
        this.ratingUpdateInfo = ratingUpdateInfo;

    }

    public RatingUpdateInfo getRatingUpdateInfo() {
        return ratingUpdateInfo;
    }
}