package com.meg.atable.service.impl;

import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.repository.MealPlanRepository;
import com.meg.atable.data.repository.MealPlanSlotRepository;
import com.meg.atable.service.DishService;
import com.meg.atable.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class MealPlanServiceImpl implements MealPlanService {

    @Autowired
    UserService userService;

    @Autowired
    MealPlanRepository mealPlanRepository;

    @Autowired
    MealPlanSlotRepository slotRepository;

    @Autowired
    DishService dishService;


    public List<MealPlanEntity> getMealPlansForUserName(String username) {
        // get user
        UserAccountEntity user = userService.getUserByUserName(username);

        List<MealPlanEntity> list = mealPlanRepository.findByUserId(user.getId());

        return list;
    }

    public MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity) {
        // get username
        UserAccountEntity user = userService.getUserByUserName(username);

        // createMealPlan with repository and return
        mealPlanEntity.setUserId(user.getId());
        mealPlanEntity.setCreated(new Date());
        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);
        return mealPlanEntity;
    }

    public MealPlanEntity getMealPlanById(String userName, Long mealPlanId) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        MealPlanEntity mealPlanEntity = mealPlanRepository.findOne(mealPlanId);

        // ensure that this meal plan belongs to the user
        if (mealPlanEntity != null && mealPlanEntity.getUserId() == user.getId()) {
            return mealPlanEntity;
        }
        return null;
    }

    public void addDishToMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);
        // MM need check here for mealplan not found
        // get dish
        DishEntity dish = dishService.getDishForUserById(username, dishId).get();

        // add slot to dish
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        // add new slot
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        slotRepository.save(slot);

        slotList.add(slot);
        mealPlan.setSlots(slotList);
        mealPlanRepository.save(mealPlan);
    }

    public void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);
        // get slots
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        SlotEntity toDelete = null;
        List<SlotEntity> toSave = new ArrayList<>();
        for (SlotEntity slot : slotList) {
            if (slot.getDish().getId() == dishId) {
                toDelete = slot;
            } else {
                toSave.add(slot);
            }
        }
        // filter slot to be deleted from plan
        mealPlan.setSlots(toSave);
        mealPlanRepository.save(mealPlan);
        if (toDelete != null) {
            slotRepository.delete(toDelete);
        }


    }

    public boolean deleteMealPlan(String name, Long mealPlanId) {
        MealPlanEntity toDelete = getMealPlanById(name, mealPlanId);

        if (toDelete != null) {
            if (toDelete.getSlots() != null && toDelete.getSlots().size() > 0) {
                slotRepository.delete(toDelete.getSlots());
                toDelete.setSlots(null);
            }
            mealPlanRepository.delete(toDelete);
            return true;
        }
        return false;
    }
}
