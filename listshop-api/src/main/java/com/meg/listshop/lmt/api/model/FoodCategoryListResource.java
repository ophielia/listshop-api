package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FoodCategoryListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedFoodCategoryListResource embeddedList;

    public FoodCategoryListResource(List<FoodCategoryResource> foodResourceList) {
        this.embeddedList = new EmbeddedFoodCategoryListResource(foodResourceList);
    }

    public FoodCategoryListResource() {
    }

    public EmbeddedFoodCategoryListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedFoodCategoryListResource embeddedList) {
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
