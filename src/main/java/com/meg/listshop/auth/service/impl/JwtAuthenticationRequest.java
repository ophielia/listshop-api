package com.meg.listshop.auth.service.impl;

import java.io.Serializable;

/**
 * Created by stephan on 20.03.16.
 */
public class JwtAuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String token;

    public JwtAuthenticationRequest() {
        super();
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
