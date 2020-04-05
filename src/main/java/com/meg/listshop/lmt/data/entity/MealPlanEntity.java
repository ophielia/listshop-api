package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.MealPlanType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meal_plan")
@GenericGenerator(
        name = "meal_plan_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="meal_plan_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class MealPlanEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="meal_plan_sequence")
    @Column(name = "meal_plan_id")
    private Long mealPlanId;

    private Long userId;

    private String name;

    private Date created;

    @Enumerated(EnumType.STRING)
    private MealPlanType mealPlanType;

    @OneToMany(mappedBy = "mealPlan", fetch = FetchType.EAGER)
    private List<SlotEntity> slots = new ArrayList<>();

    private Long targetId;

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
        return slots!=null?slots:new ArrayList<>();
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

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}