package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StatisticListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("statistic")
    private List<Statistic> statistics;

    public StatisticListResource(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    public StatisticListResource() {
        // empty constructor - because jackson needs it
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "statistics";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
