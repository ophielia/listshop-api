package com.meg.listshop.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class AdminUser {

    private String email;

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("last_login")
    private Date lastLogin;
    @JsonProperty("creation_date")
    private Date created;
    @JsonProperty("list_count")
    private int listCount;
    @JsonProperty("dish_count")
    private int dishCount;
    @JsonProperty("meal_plan_count")
    private int mealPlanCount;

    public AdminUser() {
        // empty constructor, because Jackson likes it that way.
    }

    public AdminUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public AdminUser email(String email) {
        this.email = email;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public AdminUser userId(String userId) {
        this.userId = userId;
        return this;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public AdminUser lastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public AdminUser created(Date created) {
        this.created = created;
        return this;
    }

    public int getListCount() {
        return listCount;
    }

    public AdminUser listCount(int listCount) {
        this.listCount = listCount;
        return this;
    }

    public int getDishCount() {
        return dishCount;
    }

    public AdminUser dishCount(int dishCount) {
        this.dishCount = dishCount;
        return this;
    }

    public int getMealPlanCount() {
        return mealPlanCount;
    }

    public AdminUser mealPlanCount(int mealPlanCount) {
        this.mealPlanCount = AdminUser.this.mealPlanCount;
        return this;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                ",userId='" + userId + '\'' +
                "  email='" + email + '\'' +
                ", lastLogin=" + lastLogin +
                ", created=" + created +
                ", listCount=" + listCount +
                ", dishCount=" + dishCount +
                ", mealPlanCount=" + mealPlanCount +
                '}';
    }
}
