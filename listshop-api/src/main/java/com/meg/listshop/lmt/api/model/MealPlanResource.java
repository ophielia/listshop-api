package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MealPlanResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("meal_plan")
    private MealPlan mealPlan;

    public MealPlanResource(MealPlan mealPlan) {
        this.mealPlan = mealPlan;

    }

    public MealPlanResource() {
        // empty constructor
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }

    @Override
    @JsonIgnore
    public String getRootPath() {
        return "mealplan";
    }

    @Override
    @JsonIgnore
    public String getResourceId() {
        return String.valueOf(mealPlan.getMealPlanId());
    }
}