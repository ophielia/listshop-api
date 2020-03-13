package com.meg.atable.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Role {

    private String name;

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
