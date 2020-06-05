package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutCreateUser {

    private User user;
    private ClientDeviceInfo deviceInfo;

    public PutCreateUser() {
    }

    public User getUser() {
        return user;
    }

    @JsonProperty("device_info")
    public ClientDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
