package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 09/10/2017.
 */
public enum MealPlanType {

    Manual("Manual"),
    Targeted("Targeted");

    private final String display;


    MealPlanType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }


}
