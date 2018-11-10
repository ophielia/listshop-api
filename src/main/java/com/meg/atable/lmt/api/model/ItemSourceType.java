package com.meg.atable.lmt.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ItemSourceType {
    MealPlan("MealPlan"),
    BaseList("BaseList"),
    PreviousList("PreviousList"),
    PickUpList("PickUpList"),
    Manual("Manual");

    private final String display;


    ItemSourceType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
