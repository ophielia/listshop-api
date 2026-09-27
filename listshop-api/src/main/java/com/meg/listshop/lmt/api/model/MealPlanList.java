/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class MealPlanList {

    @JsonProperty("meal_plan_list")
    private List<MealPlan> mealPlanList;

    public MealPlanList(List<MealPlan> mealPlanList) {
        this.mealPlanList = mealPlanList;
    }

    public List<MealPlan> getMealPlanList() {
        return mealPlanList;
    }

    public void setMealPlanList(List<MealPlan> mealPlanList) {
        this.mealPlanList = mealPlanList;
    }

}
