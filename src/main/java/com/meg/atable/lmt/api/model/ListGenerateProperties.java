package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class ListGenerateProperties {


    @JsonProperty("dish_sources")
    private List<String> dishSources;

    @JsonProperty("meal_plan_source")
    private String mealPlanSourceId;

    @JsonProperty("list_type")
    private String rawListType;

    @JsonProperty("add_from_base")
    private Boolean addFromBase;

    @JsonProperty("add_from_pickup")
    private Boolean addFromPickup;

    @JsonProperty("generate_mealplan")
    private Boolean generateMealplan;

    public ListGenerateProperties() {
        // empty constructor
    }

    public List<String> getRawDishSources() {
        return dishSources;
    }

    public List<Long> getDishSourcesIds() {
        if (dishSources == null) {
            return null;
        }
        return dishSources.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public void setDishSources(List<String> dishSources) {
        this.dishSources = dishSources;
    }

    public String getRawMealPlanSourceId() {
        return mealPlanSourceId;
    }

    public Long getMealPlanSourceId() {
        return mealPlanSourceId==null?null:new Long(mealPlanSourceId);
    }

    public void setMealPlanSourceId(String mealPlanSourceId) {
        this.mealPlanSourceId = mealPlanSourceId;
    }

    public Boolean getAddFromBase() {
        return addFromBase==null?false:addFromBase;
    }

    public void setAddFromBase(Boolean addFromBase) {
        this.addFromBase = addFromBase;
    }

    public Boolean getAddFromPickup() {
        return addFromPickup==null?false:addFromPickup;
    }

    public void setAddFromPickup(Boolean addFromPickup) {
        this.addFromPickup = addFromPickup;
    }

    public Boolean getGenerateMealplan() {
        return generateMealplan==null?false:generateMealplan;
    }

    public void setGenerateMealplan(Boolean generateMealplan) {
        this.generateMealplan = generateMealplan;
    }

    public String getRawListType() {
        return rawListType;
    }

    public ListType getListType() {
        if (rawListType == null) {
            return null;
        }
        return ListType.valueOf(rawListType);
    }

    public void setRawListType(String rawListType) {
        this.rawListType = rawListType;
    }
}