package com.meg.atable.lmt.api.model;


import com.meg.atable.lmt.data.entity.ListTagStatistic;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class StatisticListResource extends ResourceSupport {

    private final List<Statistic> statistics;

    public StatisticListResource(List<ListTagStatistic> statEntities) {
        statistics = new ArrayList<>();
        for (ListTagStatistic st : statEntities) {
            Statistic statistic = ModelMapper.toModel(st);
            statistics.add(statistic);
        }

    }


    public List<Statistic> getStatistic() {
        return statistics;
    }
}