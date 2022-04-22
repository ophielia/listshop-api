package com.meg.listshop.admin.model;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListResource {

    private List<AdminUser> users = new ArrayList<>();

    public AdminUserListResource() {
    }

    public AdminUserListResource(List<AdminUser> users) {
        this.users = users;
    }

    public List<AdminUser> getUsers() {
        return users;
    }
}
