package com.meg.listshop.lmt.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

public class EmbeddedDishResourceList extends RepresentationModel {

    @JsonProperty("_embedded")
    private DishResourceList embeddedList;

    public DishResourceList getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(DishResourceList embeddedList) {
        this.embeddedList = embeddedList;
    }
}