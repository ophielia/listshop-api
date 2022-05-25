package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSearchUsers {

    private String email;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("list_id")
    private String listId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }
}
