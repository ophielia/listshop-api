package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;


public class MealPlanResource extends RepresentationModel {

    @JsonProperty("meal_plan")
    private final MealPlan mealPlan;

    public MealPlanResource(MealPlan mealPlan) {
        this.mealPlan = mealPlan;

    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }
}