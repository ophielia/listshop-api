package com.meg.atable.lmt.api.model;


import com.meg.atable.lmt.data.entity.ListTagStatistic;
import org.springframework.hateoas.ResourceSupport;

public class StatisticResource extends ResourceSupport {

    private final Statistic statistic;

    public StatisticResource(ListTagStatistic statistic) {
        this.statistic = ModelMapper.toModel(statistic);

    }


    public Statistic getStatistic() {
        return statistic;
    }
}