package com.meg.listshop.lmt.service;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingInfo;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;

import java.util.*;
import java.util.stream.Collectors;


public class ServiceTestUtils {

    public static TagEntity buildTag(Long id,String tagname,  TagType tagtype) {
        TagEntity tagEntity = new TagEntity(id);
        tagEntity.setName(tagname);
        tagEntity.setTagType(tagtype);
        return tagEntity;
    }

    public static DishItemEntity buildDishItemFromTag(Long id, TagEntity tag) {
        DishItemEntity itemEntity = new DishItemEntity(id);
        itemEntity.setTag(tag);
        return itemEntity;
    }

    public static  SlotEntity buildDishSlot(MealPlanEntity mealplan, DishEntity dish) {
        SlotEntity slot = new SlotEntity();
        slot.setDish(dish);
        slot.setMealPlan(mealplan);
        return slot;
    }


    public static DishEntity buildDishWithTags(Long userId, String dishName, List<TagEntity> tags) {
        DishEntity dish = new DishEntity();
        dish.setUserId(userId);
        dish.setDishName(dishName);
        dish.setItems(tags.stream().map(t -> buildDishItemFromTag(t.getId(), t)).collect(Collectors.toList()));
        return dish;
    }

    public static DishEntity buildDish(Long userId, String dishName, List<DishItemEntity> items) {
        DishEntity dish = new DishEntity();
        dish.setUserId(userId);
        dish.setDishName(dishName);
        dish.setItems(items);
        return dish;
    }


    public static ListLayoutEntity buildListLayout(Long id, String name) {
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity(id);
        listLayoutEntity.setName(name);
        return listLayoutEntity;
    }

    public static ListLayoutCategoryEntity buildListCategory(Long id, String name, ListLayoutEntity listLayout) {
        ListLayoutCategoryEntity layoutCategoryEntity = new ListLayoutCategoryEntity(id);
        layoutCategoryEntity.setLayoutId(listLayout.getId());
        layoutCategoryEntity.setName(name);
        return layoutCategoryEntity;
    }

    public static ListItemEntity buildItem(Long itemId, TagEntity tagEntity, Long listId) {
        ListItemEntity listItemEntity = new ListItemEntity(itemId);
        listItemEntity.setTag(tagEntity);
        listItemEntity.setListId(listId);
        listItemEntity.setAddedOn(new Date());

        return listItemEntity;
    }

    public static ShoppingListEntity buildShoppingList(Long userId, Long listId) {
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);
        shoppingList.setItems(new ArrayList<>());
        return shoppingList;
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


    public static TagEntity buildTagEntity(Long tagId, String tagName,
                                           TagType tagType) {
        return buildTagEntity(tagId, tagName, tagType, 0.0);
    }

    public static TagEntity buildTagEntity(Long tagId, String tagName,
                                           TagType tagType, Double power) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName(tagName);
        tag.setTagType(tagType);
        tag.setPower(power);
        return tag;
    }

    public static DishItemEntity buildDishItemFromTag(Long dishItemId, Long tagId, String tagName,
                                                      TagType tagType, Double power) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName(tagName);
        tag.setTagType(tagType);
        tag.setPower(power);
        return buildDishItemFromTag(dishItemId, tag);
    }

    public static DishItemEntity buildDishItemFromTag(long dishItemId, long tagId, String tagName, TagType tagType) {
        return buildDishItemFromTag(dishItemId, tagId, tagName, tagType, null);
    }

    public static RatingUpdateInfo buildDummyRatingUpdateInfo() {
        return new RatingUpdateInfo(Collections.emptySet(), Collections.emptySet());
    }
}