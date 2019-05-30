package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statistic {

    @JsonProperty("list_tag_stat_id")
    private Long listTagStatId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("tag_id")
    private Long tagId;
    @JsonProperty("added_count")
    private Integer addedCount;
    @JsonProperty("removed_count")
    private Integer removedCount;
    @JsonProperty("added_to_dish_count")
    private Integer addedToDish;


    public Statistic(Long listTagStatId) {
        this.listTagStatId = listTagStatId;
    }

    public Long getUserId() {
        return userId;
    }

    public Statistic userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getTagId() {
        return tagId;
    }

    public Statistic tagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }

    public Integer getAddedCount() {
        return addedCount;
    }

    public Statistic addedCount(Integer addedCount) {
        this.addedCount = addedCount;
        return this;
    }

    public Integer getRemovedCount() {
        return removedCount;
    }

    public Statistic removedCount(Integer removedCount) {
        this.removedCount = removedCount;
        return this;
    }

    public Integer getAddedToDish() {
        return addedToDish;
    }

    public Statistic addedToDish(Integer addedToDish) {
        this.addedToDish = addedToDish;
        return this;
    }
}