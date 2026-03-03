package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LegendSource {

    @JsonProperty("list_id")
    private String listId;
    @JsonProperty("dish_id")
    private String dishId;
    private String display;


    public LegendSource() {
        // empty constructor for jackson
    }

    public LegendSource(String listId, String dishId, String display) {
        this.listId = listId;
        this.dishId = dishId;
        this.display = display;
    }

    @Override
    public String toString() {
        return "LegendSource{" +
                "list_id='" + listId + '\'' +
                ", dish_id='" + dishId + '\'' +
                ", display='" + display + '\'' +
                '}';
    }
}
