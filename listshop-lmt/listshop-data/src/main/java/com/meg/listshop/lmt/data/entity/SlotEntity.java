package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plan_slot")
public class SlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_plan_slot_sequence")
    @SequenceGenerator(name = "meal_plan_slot_sequence", sequenceName = "meal_plan_slot_sequence", allocationSize = 1)
    @Column(name = "meal_plan_slot_id")
    private Long mealPlanSlotId;

    @OneToOne
    private DishEntity dish;

    @ManyToOne
    @JoinColumn(name = "MEAL_PLAN_ID", nullable = false)
    private MealPlanEntity mealPlan;

    public SlotEntity() {
        // jpa empty constructor
    }

    public Long getMealPlanSlotId() {
        return mealPlanSlotId;
    }

    public DishEntity getDish() {
        return dish;
    }

    public void setDish(DishEntity dish) {
        this.dish = dish;
    }

    public MealPlanEntity getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlanEntity mealPlan) {
        this.mealPlan = mealPlan;
    }
}