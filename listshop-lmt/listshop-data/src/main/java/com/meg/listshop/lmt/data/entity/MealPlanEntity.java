package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.MealPlanType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meal_plan")
@NamedEntityGraph(
        name = "mealplan-dish-entity-graph",
        attributeNodes = @NamedAttributeNode(value = "slots", subgraph = "subgraph.dish"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.dish",
                        attributeNodes = @NamedAttributeNode(value = "dish"))})
public class MealPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_plan_sequence")
    @SequenceGenerator(name = "meal_plan_sequence", sequenceName = "meal_plan_sequence", allocationSize = 1)
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

    public void setId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
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
        return slots != null ? slots : new ArrayList<>();
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