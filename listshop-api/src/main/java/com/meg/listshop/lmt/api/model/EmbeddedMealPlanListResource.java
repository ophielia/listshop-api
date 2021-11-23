package com.meg.listshop.lmt.api.model;


import java.util.List;

public class EmbeddedMealPlanListResource {

    private List<MealPlan> mealPlanResourceList;

    public EmbeddedMealPlanListResource(List<MealPlan> tagResourceList) {
        this.mealPlanResourceList = tagResourceList;
    }


    public List<MealPlan> getMealPlanResourceList() {
        return mealPlanResourceList;
    }

    public void setMealPlanResourceList(List<MealPlan> tagResourceList) {
        this.mealPlanResourceList = tagResourceList;
    }


}