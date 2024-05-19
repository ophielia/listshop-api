package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RatingInfo {

    private Long ratingTagId;

    private String ratingTagLabel;

    private Integer power;
    private Integer maxPower;

    public RatingInfo() {
    }

    public RatingInfo(Long tagId, String name) {
        this.ratingTagId = tagId;
        this.ratingTagLabel = name;
    }

    public RatingInfo(Long id, String name, Integer power) {
        this.ratingTagLabel = name;
        this.ratingTagId = id;
        this.power = power;
    }

    @JsonProperty("rating_tag_id")
    public Long getRatingTagId() {
        return ratingTagId;
    }

    public void setRatingTagId(Long ratingTagId) {
        this.ratingTagId = ratingTagId;
    }

    @JsonProperty("label")
    public String getRatingTagLabel() {
        return ratingTagLabel;
    }

    public void setRatingTagLabel(String ratingTagLabel) {
        this.ratingTagLabel = ratingTagLabel;
    }

    @JsonProperty("power")
    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    @JsonProperty("max_power")
    public void setMaxPower(Integer maxPower) {
        this.maxPower = maxPower;
    }

    public Integer getMaxPower() {
        return maxPower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingInfo that = (RatingInfo) o;
        return ratingTagId.equals(that.ratingTagId) &&
                Objects.equals(ratingTagLabel, that.ratingTagLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratingTagId, ratingTagLabel);
    }

}
