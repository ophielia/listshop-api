package com.meg.listshop.auth.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meg.listshop.lmt.api.model.AbstractListShopResource;
import com.meg.listshop.lmt.api.model.ListShopModel;

public class UserResource extends AbstractListShopResource implements ListShopModel {

    private final User user;

    public UserResource(User dish) {
        this.user = dish;
    }

    public User getUser() {
        return user;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "user";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return user.getEmail().toString();
    }
}