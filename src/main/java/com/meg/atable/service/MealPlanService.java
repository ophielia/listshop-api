package com.meg.atable.service;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanService {

    boolean deleteMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    List<MealPlanEntity> getMealPlansForUserName(String username);

    MealPlanEntity getMealPlanById(String userName, Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException;

    MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity);

    MealPlanEntity createMealPlanFromProposal(String name, Long proposalId);

    void addDishToMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;

    void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;


    List<TagEntity> fillInDishTags(MealPlanEntity mealPlan);

    void updateLastAddedDateForDishes(MealPlanEntity mealPlan);

    List<TagEntity> getTagsForSlot(SlotEntity slot);

    void renameMealPlan(String userName, Long mealPlanId, String newName) throws ObjectNotFoundException, ObjectNotYoursException;
}
