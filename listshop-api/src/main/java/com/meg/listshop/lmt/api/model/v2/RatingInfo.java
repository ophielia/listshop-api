package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({ "tag", "power", "max_power" })
public class RatingInfo {

    @JsonProperty("tag")
    private NestedTag tag;
    private Integer power;
    @JsonProperty("max_power")
    private Integer maxPower;

    public RatingInfo() {
    }


    public RatingInfo(com.meg.listshop.lmt.api.model.RatingInfo ratingInfo) {
        this.tag = new NestedTag(ratingInfo.getRatingTagId(), ratingInfo.getRatingTagLabel());
        this.power = ratingInfo.getPower();
        this.maxPower = ratingInfo.getMaxPower();
    }

    public RatingInfo withTag(NestedTag tag) {
        this.tag = tag;
        return this;
    }

    public RatingInfo withPower(Integer power) {
        this.power = power;
        return this;
    }

    public RatingInfo withMaxPower(Integer maxPower) {
        this.maxPower = maxPower;
        return this;
    }

    public NestedTag getTag() {
        return tag;
    }

    public Integer getPower() {
        return power;
    }

    public Integer getMaxPower() {
        return maxPower;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RatingInfo that = (RatingInfo) o;
        return Objects.equals(tag, that.tag) && Objects.equals(power, that.power) && Objects.equals(maxPower, that.maxPower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, power, maxPower);
    }

    @Override
    public String toString() {
        return "RatingInfo{" +
                "tag=" + tag +
                ", power=" + power +
                ", maxPower=" + maxPower +
                '}';
    }
}
