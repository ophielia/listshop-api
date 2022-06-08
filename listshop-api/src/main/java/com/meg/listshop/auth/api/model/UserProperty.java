/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

public class UserProperty {


    private String key;
    private String value;

    public UserProperty() {
        // empty constructor for jackson
    }

    public UserProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
