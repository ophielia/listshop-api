package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StatisticListPost {

    @JsonProperty("statistics")
    private List<Statistic> statisticList;


    public StatisticListPost() {
    }

    public List<Statistic> getStatisticList() {
        return statisticList;
    }

    public void setStatisticList(List<Statistic> statisticList) {
        this.statisticList = statisticList;
    }
}