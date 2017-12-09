package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 24/11/2017.
 */
public enum GenerateType {

    Add("Add"),
    Replace("Replace");
    
    private final String display;


    GenerateType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}
