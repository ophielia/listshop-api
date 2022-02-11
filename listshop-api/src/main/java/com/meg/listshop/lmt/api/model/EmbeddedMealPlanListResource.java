package com.meg.listshop.lmt.api.model;


import java.util.List;

public class EmbeddedMealPlanListResource {

    private List<MealPlanResource> mealPlanResourceList;

    public EmbeddedMealPlanListResource(List<MealPlanResource> tagResourceList) {
        this.mealPlanResourceList = tagResourceList;
    }


    public List<MealPlanResource> getMealPlanResourceList() {
        return mealPlanResourceList;
    }

    public void setMealPlanResourceList(List<MealPlanResource> tagResourceList) {
        this.mealPlanResourceList = tagResourceList;
    }


}