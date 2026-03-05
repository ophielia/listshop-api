package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;

public record RatingsDTO(List<TagInfoDTO> ratingTags, List<TagEntity> ratingHeaders, Integer maxRatingPower) {
    public RatingsDTO(List<TagInfoDTO> ratingTags, List<TagEntity> ratingHeaders, Integer maxRatingPower) {
        this.ratingTags = ratingTags;
        this.ratingHeaders = ratingHeaders;
        this.maxRatingPower = maxRatingPower;
    }

    public List<TagInfoDTO> getRatingTags() {
        return ratingTags;
    }

    public List<TagEntity> getRatingHeaders() {
        return ratingHeaders;
    }

    public Integer getMaxRatingPower() {
        return maxRatingPower;
    }
}
