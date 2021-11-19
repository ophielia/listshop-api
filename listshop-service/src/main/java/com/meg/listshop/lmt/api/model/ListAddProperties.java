package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListAddProperties {


    @JsonProperty("dish_sources")
    private List<String> dishSources;


    public ListAddProperties() {
        // empty constructor
    }

    public List<String> getDishSources() {
        return dishSources;
    }

    public void setDishSources(List<String> dishSources) {
        this.dishSources = dishSources;
    }

    public List<Long> getDishSourceIds() {
        if (dishSources == null) {
            return new ArrayList<Long>();
        }
        return dishSources.stream().map(Long::valueOf).collect(Collectors.toList());
    }
}