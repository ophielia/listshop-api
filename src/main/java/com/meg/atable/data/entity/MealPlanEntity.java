package com.meg.atable.data.entity;

import com.meg.atable.api.model.MealPlanType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meal_plan")
public class MealPlanEntity {

    @Id
    @GeneratedValue
    @Column(name = "meal_plan_id")
    private Long mealPlanId;

    private Long userId;

    private String name;

    private Date created;

    @Enumerated(EnumType.STRING)
    private MealPlanType mealPlanType;

    @OneToMany(mappedBy = "mealPlan", fetch = FetchType.EAGER)
    private List<SlotEntity> slots;

    public MealPlanEntity() {
        // jpa empty constructor
    }

    public MealPlanEntity(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public Long getId() {
        return mealPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public MealPlanType getMealPlanType() {
        return mealPlanType;
    }

    public void setMealPlanType(MealPlanType mealPlanType) {
        this.mealPlanType = mealPlanType;
    }

    public List<SlotEntity> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotEntity> slots) {
        this.slots = slots;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}