package com.meg.atable.service.impl;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.*;

import java.util.Date;
import java.util.List;


public class ServiceTestUtils {

    public static TagEntity buildTag(String tagname,  TagType tagtype) {
        return buildTag(99L,tagname, tagtype);
    }

    public static TagEntity buildTag(Long id,String tagname,  TagType tagtype) {
        TagEntity tagEntity = new TagEntity(id);
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


    public static ListLayoutEntity buildListLayout(Long id, String name, ListLayoutType listlayoutType) {
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity(id);
        listLayoutEntity.setLayoutType(listlayoutType);
        listLayoutEntity.setName(name);
        return listLayoutEntity;
    }

    public static ListLayoutCategoryEntity buildListCategory(Long id, String name, ListLayoutEntity listLayout) {
        ListLayoutCategoryEntity layoutCategoryEntity = new ListLayoutCategoryEntity(id);
        layoutCategoryEntity.setLayoutId(listLayout.getId());
        layoutCategoryEntity.setName(name);
        return layoutCategoryEntity;
    }
}