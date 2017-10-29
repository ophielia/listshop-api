package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ItemSourceType {
    MealPlan("MealPlan"),
    BaseList("BaseList"),
    PickUpList("PickUpList");

    private final String display;


    ItemSourceType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
