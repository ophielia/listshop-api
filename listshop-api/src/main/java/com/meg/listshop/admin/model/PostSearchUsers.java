package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSearchUsers {

    private String email;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("list_id")
    private String listId;
}
