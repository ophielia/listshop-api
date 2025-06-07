package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 27/10/2017.
 */
public enum ItemSourceType {
    Dish("d"),
    List("l"),
    Special("s");

    private final String prefix;


    ItemSourceType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
