package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.ListLayoutType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;

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

    public static ItemEntity buildItem(Long itemId, TagEntity tagEntity, Long listId) {
        ItemEntity itemEntity = new ItemEntity(itemId);
        itemEntity.setTag(tagEntity);
        itemEntity.setListId(listId);
        itemEntity.setAddedOn(new Date());

        return itemEntity;
    }

    public static UserEntity buildUser(Long userId, String userName) {
        UserEntity user = new UserEntity(userName, "password");
        user.setId(userId);
        return user;
    }

    public static ShadowTags buildShadowTag(Long shadowTagId, Long dishId) {
        ShadowTags shadowTags = new ShadowTags();
        shadowTags.setTagId(shadowTagId);
        shadowTags.setDishId(dishId);
        return shadowTags;
    }
}