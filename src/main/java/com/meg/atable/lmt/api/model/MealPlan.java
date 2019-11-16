package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class MealPlan {

    private Long mealPlanId;

    private String name;

    private Date created;

    private String userId;

    private String mealPlanType;

    private List<Slot> slots;


    public MealPlan() {
        // empty constructor
    }


    public MealPlan(Long id) {
        this.mealPlanId = id;
    }

    @JsonProperty("meal_plan_id")
    public Long getMealPlanId() {
        return mealPlanId;
    }

    public MealPlan mealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MealPlan name(String name) {
        this.name = name;
        return this;
    }

    public Date getCreated() {
        return created;
    }


    public MealPlan created(Date created) {
        this.created = created;
        return this;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public MealPlan userId(String userId) {
        this.userId = userId;
        return this;
    }

    @JsonProperty("meal_plan_type")
    public String getMealPlanType() {
        return mealPlanType;
    }

    public MealPlan mealPlanType(String mealPlanType) {
        this.mealPlanType = mealPlanType;
        return this;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public MealPlan slots(List<Slot> slots) {
        this.slots = slots;
        return this;
    }
}