package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanService {

    void deleteMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    List<MealPlanEntity> getMealPlansForUserName(String username);

    MealPlanEntity getMealPlanById(String userName, Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException;

    MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity);

    MealPlanEntity createMealPlanFromProposal(String name, Long proposalId) throws ObjectNotYoursException, ObjectNotFoundException;

    void addDishToMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;

    void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;


    List<TagEntity> fillInDishTags(MealPlanEntity mealPlan);

    void updateLastAddedDateForDishes(MealPlanEntity mealPlan);

    void renameMealPlan(String userName, Long mealPlanId, String newName) throws ObjectNotFoundException, ObjectNotYoursException;

    RatingUpdateInfo getRatingsForMealPlan(String username, Long mealPlanId);

    MealPlanEntity copyMealPlan(String name, Long mealPlanId);
}
