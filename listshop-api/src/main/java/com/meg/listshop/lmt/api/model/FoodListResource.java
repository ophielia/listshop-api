package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FoodListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedFoodListResource embeddedList;

    public FoodListResource(List<FoodResource> foodResourceList) {
        this.embeddedList = new EmbeddedFoodListResource(foodResourceList);
    }

    public FoodListResource() {
    }

    public EmbeddedFoodListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedFoodListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "food";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
