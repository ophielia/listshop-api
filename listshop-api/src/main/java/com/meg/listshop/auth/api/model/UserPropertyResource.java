/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.AbstractListShopResource;
import com.meg.listshop.lmt.api.model.ListShopModel;

public class UserPropertyResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("user_property")
    private final UserProperty userProperty;

    public UserPropertyResource(UserProperty userProperty) {
        this.userProperty = userProperty;
    }

    public UserProperty getUserProperty() {
        return userProperty;
    }

    @Override
    public String getRootPath() {
        return "user/property";
    }

    @Override
    public String getResourceId() {
        return userProperty.getKey();
    }
}