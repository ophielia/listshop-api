package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MealPlanListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedMealPlanListResource embeddedList;

    public MealPlanListResource(List<MealPlanResource> mealPlanList) {
        this.embeddedList = new EmbeddedMealPlanListResource(mealPlanList);
    }

    public EmbeddedMealPlanListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedMealPlanListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "mealplan";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
