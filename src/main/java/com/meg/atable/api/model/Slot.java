package com.meg.atable.api.model;

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

    public Long getMealPlanSlotId() {
        return mealPlanSlotId;
    }

    public Slot mealPlanSlotId(Long mealPlanSlotId) {
        this.mealPlanSlotId = mealPlanSlotId;
        return this;
    }

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
