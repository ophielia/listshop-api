package com.meg.atable.service.impl;

import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.Date;
import java.util.List;


public class ServiceTestUtils {

    public static TagEntity buildTag(String tagname,  TagType tagtype) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagname);
        tagEntity.setTagType(tagtype);
        return tagEntity;
    }

    public static  SlotEntity buildDishSlot(MealPlanEntity mealplan, DishEntity dish) {
        SlotEntity slot = new SlotEntity();
        slot.setDish(dish);
        slot.setMealPlan(mealplan);
        return slot;
    }

    public static MealPlanEntity buildMealPlan(String mealPlanName, Long userId) {
        MealPlanEntity testMealPlan = new MealPlanEntity();
        testMealPlan.setCreated(new Date());
        testMealPlan.setName(mealPlanName);
        testMealPlan.setUserId(userId);
        return testMealPlan;
    }

    public static DishEntity buildDish(Long userId,String dishName,List<TagEntity> tags) {
        DishEntity dish = new DishEntity();
        dish.setUserId(userId);
        dish.setDishName(dishName);
        dish.setTags(tags);
        return dish;
    }


}