package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 09/10/2017.
 */
public enum TagType {

    DishType("DishType"),
    Rating("Rating"),
    TagType("TagType"),
    Ingredient("Ingredient");

    private final String display;



    TagType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }


}
