package com.meg.listshop.lmt.api.model;


import net.minidev.json.annotate.JsonIgnore;

public class RatingUpdateInfoResource extends AbstractListShopResource implements ListShopModel {

    private final RatingUpdateInfo ratingUpdateInfo;

    public RatingUpdateInfoResource(RatingUpdateInfo ratingUpdateInfo) {
        this.ratingUpdateInfo = ratingUpdateInfo;

    }

    public RatingUpdateInfo getRatingUpdateInfo() {
        return ratingUpdateInfo;
    }

    @Override
    @JsonIgnore
    public String getRootPath() {
        return "dish";
    }

    @Override
    @JsonIgnore
    public String getResourceId() {
        return null;
    }
}