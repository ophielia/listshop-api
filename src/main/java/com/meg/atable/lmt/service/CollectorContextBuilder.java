package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ContextType;
import com.meg.atable.lmt.api.model.StatisticCountType;

public class CollectorContextBuilder {

    private CollectorContext context;


    public CollectorContextBuilder create(ContextType contextType) {
        context = new CollectorContext();
        context.setContextType(contextType);
        return this;
    }

    public CollectorContextBuilder withDishId(Long dishSourceId) {
        this.context.setDishId(dishSourceId);
        return this;
    }

    public CollectorContextBuilder withStatisticCountType(StatisticCountType countType) {
        this.context.setStatisticCountType(countType);
        return this;
    }

    public CollectorContextBuilder withRemoveEntireItem(Boolean removeEntireItem) {
        this.context.setRemoveEntireItem(removeEntireItem);
        return this;
    }

    public CollectorContext build() {
        return context;
    }


    public CollectorContextBuilder withListId(Long listId) {
        this.context.setListId(listId);
        return this;
    }
}
