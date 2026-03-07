package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;

public record RatingInfoDTO(String display, Long ratingId, Double power, Double maxPower) {
    public RatingInfoDTO(String display, Long ratingId, Double power, Double maxPower) {
        this.display = display;
        this.ratingId = ratingId;
        this.power = power;
        this.maxPower = maxPower;
    }

}
