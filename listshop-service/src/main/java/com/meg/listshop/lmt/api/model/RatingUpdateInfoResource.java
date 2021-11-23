package com.meg.listshop.lmt.api.model;


import org.springframework.hateoas.RepresentationModel;

public class RatingUpdateInfoResource extends RepresentationModel {

    private final RatingUpdateInfo ratingUpdateInfo;

    public RatingUpdateInfoResource(RatingUpdateInfo ratingUpdateInfo) {
        this.ratingUpdateInfo = ratingUpdateInfo;

    }

    public RatingUpdateInfo getRatingUpdateInfo() {
        return ratingUpdateInfo;
    }
}