package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.lmt.data.entity.MealPlanEntity;
import com.meg.atable.lmt.data.entity.SlotEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanService {

    boolean deleteMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    List<MealPlanEntity> getMealPlansForUserName(String username);

    MealPlanEntity getMealPlanById(String userName, Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException;

    MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity);

    MealPlanEntity createMealPlanFromProposal(String name, Long proposalId) throws ObjectNotYoursException, ObjectNotFoundException;

    void addDishToMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;

    void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException;


    List<TagEntity> fillInDishTags(MealPlanEntity mealPlan);

    void updateLastAddedDateForDishes(MealPlanEntity mealPlan);

    List<TagEntity> getTagsForSlot(SlotEntity slot);

    void renameMealPlan(String userName, Long mealPlanId, String newName) throws ObjectNotFoundException, ObjectNotYoursException;
}
