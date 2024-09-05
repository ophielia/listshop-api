package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plan_slot")
@GenericGenerator(
        name = "meal_plan_slot_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="meal_plan_slot_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class SlotEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="meal_plan_slot_sequence")
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