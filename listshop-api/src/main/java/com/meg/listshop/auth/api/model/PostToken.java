/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostToken {


    private String token;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("token_parameter")
    private String tokenParameter;

    public PostToken() {
        // empty constructor for json
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenParameter() {
        return tokenParameter;
    }

    public void setTokenParameter(String tokenParameter) {
        this.tokenParameter = tokenParameter;
    }
}
