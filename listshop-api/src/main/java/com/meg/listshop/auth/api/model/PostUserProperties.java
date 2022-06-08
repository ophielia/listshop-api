/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PostUserProperties {

    @JsonProperty("user_properties")
    private List<UserProperty> properties;

    public PostUserProperties() {
        // empty constructor for jackson
    }

    public List<UserProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<UserProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "PostUserProperties{" +
                "properties=" + properties +
                '}';
    }
}
