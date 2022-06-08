package com.meg.listshop.auth.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.AbstractListShopResource;
import com.meg.listshop.lmt.api.model.ListShopModel;

import java.util.List;

public class UserPropertiesResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("user_properties")
    private final List<UserProperty> userProperties;

    public UserPropertiesResource(List<UserProperty> userProperties) {
        this.userProperties = userProperties;
    }

    public List<UserProperty> getUserProperties() {
        return userProperties;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "user/";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return "properties";
    }
}