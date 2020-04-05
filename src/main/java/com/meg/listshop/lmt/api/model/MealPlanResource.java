package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.web.controller.MealPlanRestController;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class MealPlanResource extends ResourceSupport {

    @JsonProperty("meal_plan")
    private final MealPlan mealPlan;

    public MealPlanResource(MealPlanEntity mealPlanEntity)  {
        this.mealPlan = ModelMapper.toModel(mealPlanEntity);

        Long userId = mealPlanEntity.getUserId();
        this.add(linkTo(MealPlanRestController.class, userId).withRel("mealPlan"));
        this.add(linkTo(methodOn(MealPlanRestController.class, userId)
                .readMealPlan(null, mealPlan.getMealPlanId())).withSelfRel());
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }
}