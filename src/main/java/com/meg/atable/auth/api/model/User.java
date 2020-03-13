package com.meg.atable.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class User {

    private String email;

    @JsonProperty("creation_date")
    private Date creationDate;

    @JsonProperty("user_name")
    private String username;

    private String password;
    private String[] roles ;
    private String token;

    public User(String username, String email) {
        this.email = email;
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getUsername() {
        return username;
    }

    public User creationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public User roles(String[] roles) {
        this.roles = roles;
        return this;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getToken() {
        return token;
    }

    public User token(String token) {
        this.token = token;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }
    public User username(String username) {
        this.username = username;
        return this;
    }


}
