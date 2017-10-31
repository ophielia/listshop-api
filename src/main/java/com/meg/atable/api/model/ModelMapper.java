package com.meg.atable.api.model;

import com.meg.atable.data.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ModelMapper {
    private ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static Dish toModel(DishEntity dishEntity) {
        List<Tag> tags = toModel(dishEntity.getTags());
        return new Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .tags(tags)
                .userId(dishEntity.getUserId());
    }

    private static List<Tag> toModel(List<TagEntity> tagEntities) {
        List<Tag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toModel(entity));
        }
        return tags;
    }

    public static Tag toModel(TagEntity tagEntity) {
        return new Tag(tagEntity.getId())
                .name(tagEntity.getName())
                .description(tagEntity.getDescription())
                .tagType(tagEntity.getTagType().name())
                .ratingFamily(tagEntity.getRatingFamily());
    }

    public static TagEntity toEntity(Tag tag) {
        Long tagId = tag != null && tag.getId() != null ? new Long(tag.getId()) : null;
        TagEntity tagEntity = new TagEntity(tagId);

        tagEntity.setName(tag.getName());
        tagEntity.setDescription(tag.getDescription());
        tagEntity.setTagType(TagType.valueOf(tag.getTagType()));
        tagEntity.setRatingFamily(tag.getRatingFamily());

        return tagEntity;
    }

    public static TagExtended toExtendedModel(TagEntity tagEntity) {
        return new TagExtended(tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getDescription(),
                tagEntity.getTagType(),
                tagEntity.getRatingFamily(),
                tagEntity.getParentId(),
                tagEntity.getChildrenIds());
    }

    public static MealPlanEntity toEntity(MealPlan mealPlan) {
        Long mealPlanId = mealPlan != null && mealPlan.getMealPlanId() != null ? Long.valueOf(mealPlan.getMealPlanId()) : null;
        MealPlanEntity mealPlanEntity = new MealPlanEntity(mealPlanId);

        mealPlanEntity.setName(mealPlan.getName());
        mealPlanEntity.setMealPlanType(MealPlanType.valueOf(mealPlan.getMealPlanType()));
        if (mealPlan.getUserId() != null) {
            mealPlanEntity.setUserId(new Long(mealPlan.getUserId()));
        }

        return mealPlanEntity;
    }

    public static MealPlan toModel(MealPlanEntity mealPlanEntity) {
        List<Slot> slots = slotsToModel(mealPlanEntity.getSlots());
        return new MealPlan(mealPlanEntity.getId())
                .name(mealPlanEntity.getName())
                .mealPlanType(mealPlanEntity.getMealPlanType().name())
                .userId(mealPlanEntity.getUserId().toString())
                .slots(slots);

    }

    public static Slot toModel(SlotEntity slotEntity) {
        return new Slot(slotEntity.getMealPlanSlotId())
                .mealPlanId(slotEntity.getMealPlan().getId())
                .dish(toModel(slotEntity.getDish()));
    }

    private static List<Slot> slotsToModel(List<SlotEntity> slots) {
        List<Slot> slotList = new ArrayList<>();
        if (slots == null) {
            return slotList;
        }
        for (SlotEntity entity : slots) {
            slotList.add(toModel(entity));
        }
        return slotList;
    }

    public static ItemEntity toEntity(Item input) {
        //MM implement this
        return null;
    }

    public static ShoppingListEntity toEntity(ShoppingList shoppingList) {
       // MM implement this
        return null;
    }
}
