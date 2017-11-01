package com.meg.atable.service;

import com.meg.atable.data.entity.MealPlanEntity;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanService {

    boolean deleteMealPlan(String name, Long mealPlanId);

    List<MealPlanEntity> getMealPlansForUserName(String username);

    MealPlanEntity getMealPlanById(String userName, Long mealPlanId);

    MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity);

    void addDishToMealPlan(String username, Long mealPlanId, Long dishId);

    void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId);


    void fillInDishTags(MealPlanEntity mealPlan);
}
