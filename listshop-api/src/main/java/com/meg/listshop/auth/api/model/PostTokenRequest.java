/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostTokenRequest {

    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("token_parameter")
    private String tokenParameter;

    public PostTokenRequest() {
        // empty constructor for json
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
