package com.meg.listshop.auth.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.auth.api.model.ClientDeviceInfo;

import java.io.Serializable;

/**
 * Created by stephan on 20.03.16.
 */
public class JwtAuthorizationRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String username;
    private String password;
    private ClientDeviceInfo deviceInfo;

    public JwtAuthorizationRequest() {
        super();
    }

    public JwtAuthorizationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("device_info")
    public ClientDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(ClientDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
