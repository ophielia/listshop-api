/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutCreateUser {

    private User user;
    @JsonProperty("device_info")
    private ClientDeviceInfo deviceInfo;

    public PutCreateUser() {
        // empty constructor for jackson
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public ClientDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(ClientDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
