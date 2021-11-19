package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by margaretmartin on 20/10/2017.
 */
public class Slot {

    private Long mealPlanSlotId;

    private Long mealPlanId;

    private Dish dish;

    public Slot() {
    }

    public Slot(Long mealPlanSlotId) {
        this.mealPlanSlotId = mealPlanSlotId;
    }

    @JsonProperty("slot_id")
    public Long getMealPlanSlotId() {
        return mealPlanSlotId;
    }

    public Slot mealPlanSlotId(Long mealPlanSlotId) {
        this.mealPlanSlotId = mealPlanSlotId;
        return this;
    }

    @JsonProperty("meal_plan_id")
    public Long getMealPlanId() {
        return mealPlanId;
    }

    public Slot mealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
        return this;
    }

    public Dish getDish() {
        return dish;
    }

    public Slot dish(Dish dish) {
        this.dish = dish;
        return this;
    }
}
