package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class ListGenerateProperties {


    @JsonProperty("dish_sources")
    private List<String> dishSources;

    @JsonProperty("meal_plan_source")
    private String mealPlanSourceId;

    @JsonProperty("add_from_starter")
    private Boolean addFromStarter;

    @JsonProperty("generate_mealplan")
    private Boolean generateMealplan;

    @JsonProperty("list_name")
    private String listName;

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
        return mealPlanSourceId == null ? null : Long.valueOf(mealPlanSourceId);
    }

    public void setMealPlanSourceId(String mealPlanSourceId) {
        this.mealPlanSourceId = mealPlanSourceId;
    }

    public Boolean getAddFromStarter() {
        return addFromStarter == null ? false : addFromStarter;
    }

    public void setAddFromStarter(Boolean addFromBase) {
        this.addFromStarter = addFromBase;
    }

    public Boolean getGenerateMealplan() {
        return generateMealplan==null?false:generateMealplan;
    }

    public void setGenerateMealplan(Boolean generateMealplan) {
        this.generateMealplan = generateMealplan;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}