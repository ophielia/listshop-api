package com.meg.atable.auth.api.model;

public class User {

    private String email;

    private String password;

    private String username;

    public User(String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
