/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.model;

import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.lmt.api.model.v2.Dish;
import com.meg.listshop.lmt.api.model.v2.RatingInfo;
import com.meg.listshop.lmt.api.model.v2.Tag;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.*;

import java.util.*;


public class V2ModelMapper {
    private static final String DISH_PREFIX = "d";
    private static final String LIST_PREFIX = "l";

    private V2ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static Ingredient toModel(DishItemDTO ingredientDto) {
        if (ingredientDto == null) {
            return null;
        }

        NestedTag tag = new NestedTag(ingredientDto.getTagId(), ingredientDto.getTagDisplay());
        String display;
        Amount amount = null;
        if (ingredientDto.getQuantity() != null && ingredientDto.getQuantity() > 0) {
            String quantityDisplay = ingredientDto.getQuantityDisplay();
            amount = new Amount()
                    .withFractionalQuantity(fractionTypeToName(ingredientDto.getFractionalQuantity()))
                    .withWholeQuantity(ingredientDto.getWholeQuantity())
                    .withQuantity(ingredientDto.getQuantity())
                    .withUnitDisplay(ingredientDto.getUnitName())
                    .withUnitId(String.valueOf(ingredientDto.getUnitId()))
                    .withQuantityDisplay(quantityDisplay)
                    .withModifiers(ingredientDto.getRawModifiers())
                    .withDisplay(ingredientDto.getRawEntry());

            display = String.format("%s %s", ingredientDto.getRawEntry(), ingredientDto.getTagDisplay()).trim();
        } else {
            display = ingredientDto.getTagDisplay();

        }


        return new Ingredient()
                .withItemId(String.valueOf(ingredientDto.getDishItemId()))
                .withTag(tag)
                .withAmount(amount)
                .withDisplay(display);
    }

    private static String fractionTypeToName(FractionType fractionType) {
        if (fractionType == null) {
            return null;
        }
        return fractionType.name();
    }

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity, List<ShoppingListCategory> itemCategories) {
        //MM lots of model work to get details back to v1 sources
        //MM possibly - split into v1 / v2 mappers??  this guys getting long
        //MM this is the V1 return - which will(obviously) produce the same output from
        // the new input. The V2 return will be done with views
        List<LegendSource> legendSources = new ArrayList<>();
        if (shoppingListEntity.getDishSources() != null &&
                !shoppingListEntity.getDishSources().isEmpty()) {
            Set<LegendSource> dishLegends = new HashSet<>();
            shoppingListEntity.getDishSources().forEach(d -> {
                String key = V2ModelMapper.DISH_PREFIX + d.getId();

                dishLegends.add(new LegendSource(key, d.getDishName()));
            });
            legendSources.addAll(dishLegends);
        }
        if (shoppingListEntity.getListSources() != null &&
                !shoppingListEntity.getListSources().isEmpty()) {
            Set<LegendSource> listLegends = new HashSet<>();
            shoppingListEntity.getListSources().forEach(d -> {
                String key = V2ModelMapper.LIST_PREFIX + d.getId();

                listLegends.add(new LegendSource(key, d.getName()));
            });
            legendSources.addAll(listLegends);
        }

        enhanceCategories(itemCategories);

        long itemCount = 0;
        if (itemCategories != null) {
            itemCount = itemCategories.stream()
                    .map(ShoppingListCategory::getItems)
                    .flatMap(Collection::stream)
                    .filter(item -> item.getRemoved() == null && item.getCrossedOff() == null)
                    .count();
        }
        String layoutId = shoppingListEntity.getListLayoutId() != null ? String.valueOf(shoppingListEntity.getListLayoutId()) : null;
        return new ShoppingList(shoppingListEntity.getId())
                .createdOn(shoppingListEntity.getCreatedOn())
                .categories(itemCategories)
                .legendSources(legendSources)
                .isStarterList(shoppingListEntity.getIsStarterList())
                .name(shoppingListEntity.getName())
                .layoutId(layoutId)
                .updated(shoppingListEntity.getLastUpdate())
                .itemCount((int) itemCount)
                .userId(shoppingListEntity.getUserId());

    }

    public static Item toModel(ListItemEntity listItemEntity) {
        return new Item(listItemEntity.getId())
                //.tag(toModel(listItemEntity.getTag()))
                .listId(listItemEntity.getListId().toString())
                .addedOn(listItemEntity.getAddedOn())
                .updated(listItemEntity.getUpdatedOn())
                .removed(listItemEntity.getRemovedOn())
                .crossedOff(listItemEntity.getCrossedOff())
                .usedCount(listItemEntity.getUsedCount())
                .handles(listItemEntity.getHandles());
    }

    public static Dish toModel(DishDTO dishDto, boolean includeTags) {
        // tags
        List<NestedTag> dishTags = new ArrayList<>();
        if (includeTags) {
            dishTags = toModelItemsAsTags(dishDto.getTags());
        }
        // ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        if (includeTags) {
            ingredients = toIngredientsModel(dishDto.getIngredients());
        }
        // ratings
        List<RatingInfo> dishRatings = toRatingsModel(dishDto.getRatings());

        return new Dish(dishDto.getDish().getId())
                .withDescription(dishDto.getDish().getDescription())
                .withDishName(dishDto.getDish().getDishName())
                .withReference(dishDto.getDish().getReference())
                .withTags(dishTags)
                .withRatings(dishRatings)
                .withIngredients(ingredients)
                .withLastAdded(dishDto.getDish().getLastAdded())
                .withUserId(String.valueOf(dishDto.getDish().getUserId()));
    }


    public static NestedDish toV2NestedDishModel(DishEntity dishEntity) {
        return new NestedDish(dishEntity.getId(), dishEntity.getDishName());
    }

    private static void enhanceSources(List<ShoppingListItem> items) {
        if (items == null) {
            return;
        }

        items.forEach(i -> {
            List<String> sourceList = i.getSources().stream()
                    .map(V2ModelMapper::toV1SourceTags)
                    .flatMap(List::stream)
                    .toList();
            i.sourceKeys(sourceList);
        });

    }

    private static List<String> toV1SourceTags(ListItemSource s) {
        List<String> tags = new ArrayList<>();
        if (s.getLinkedDishId() != null && !s.getLinkedDishId().isEmpty()) {
            tags.add(ItemSourceType.Dish.getPrefix() + s.getLinkedDishId());
        }
        if (s.getLinkedListId() != null && !s.getLinkedListId().isEmpty()) {
            tags.add(ItemSourceType.List.getPrefix() + s.getLinkedListId());
        }
        return tags;
    }

    private static List<Ingredient> toIngredientsModel(List<DishItemDTO> ingredientDTOs) {
        if (ingredientDTOs == null) {
            return new ArrayList<>();
        }
        List<Ingredient> ingredients = new ArrayList<>();
        for (DishItemDTO dishItemDTO : ingredientDTOs) {
            ingredients.add(toModel(dishItemDTO));
        }
        return ingredients;
    }

    private static List<RatingInfo> toRatingsModel(List<RatingInfoDTO> ratings) {
        List<RatingInfo> ratingInfo = new ArrayList<>();
        if (ratings == null) {
            return ratingInfo;
        }
        return ratings.stream()
                .map(V2ModelMapper::toRatingModel)
                .toList();
    }

    private static RatingInfo toRatingModel(RatingInfoDTO dto) {
        RatingInfo info = new RatingInfo();
        if (dto == null) {
            return info;
        }
        NestedTag tag = new NestedTag(dto.ratingId(), dto.display());
        int power = (int) dto.power().doubleValue();
        int maxPower = 5;
        if (dto.maxPower() != null) {
            maxPower = (int) dto.maxPower().doubleValue();
        }
        info = info.withPower(power)
                .withMaxPower(maxPower)
                .withTag(tag);
        return info;
    }

    private static void enhanceCategories(List<ShoppingListCategory> filledCategories
    ) {
        if (filledCategories == null) {
            return;
        }
        // go through list, converting items in categories to Items
        filledCategories.forEach(c -> enhanceSources(c.getItems()));
    }

    private static List<NestedTag> toModelItemsAsTags(List<DishItemEntity> itemEntities) {
        if (itemEntities == null) {
            return new ArrayList<>();
        }
        return toNestedTagModel(itemEntities.stream().map(DishItemEntity::getTag).toList());
    }

    private static List<NestedTag> toNestedTagModel(List<TagEntity> tagEntities) {
        List<NestedTag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toNestedTagModel(entity));
        }
        return tags;
    }

    public static NestedTag toNestedTagModel(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }

        return new NestedTag(tagEntity.getId(), tagEntity.getName());
    }

    public static Suggestion toModel(SuggestionDTO suggestionDTO) {
        Suggestion suggestion = new Suggestion();
        suggestion.setText(suggestionDTO.getText());
        suggestion.setReferenceId(String.valueOf(suggestionDTO.getReferenceId()));
        if (suggestionDTO.getModifierType() != null) {
            suggestion.setModifierType(suggestionDTO.getModifierType().name());
        }
        return suggestion;
    }

    public static Tag toModel(TagInfoDTO tagInfoDTO) {
        return new Tag(tagInfoDTO.getTagId())
                .withUserId(String.valueOf(tagInfoDTO.getUserId()))
                .withName(tagInfoDTO.getName())
                .withDescription(tagInfoDTO.getDescription())
                .withTagType(tagInfoDTO.getTagType())
                .withPower(tagInfoDTO.getPower())
                .withGroup(tagInfoDTO.isGroup())
                .withParentId(String.valueOf(tagInfoDTO.getParentId()));
    }


    public static Tag toModel(TagEntity entity) {
        return new Tag(entity.getId())
                .withUserId(String.valueOf(entity.getUserId()))
                .withName(entity.getName())
                .withDescription(entity.getDescription())
                .withTagType(entity.getTagType().name())
                .withPower(entity.getPower());
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        Long tagId = tag.getTagId() != null ? Long.valueOf(tag.getTagId()) : null;
        TagEntity tagEntity = new TagEntity(tagId);

        tagEntity.setName(tag.getName().trim());
        tagEntity.setDescription(tag.getDescription());
        if (tag.getTagType() != null) {
            tagEntity.setTagType(TagType.valueOf(tag.getTagType()));
        }
        tagEntity.setPower(tag.getPower());

        return tagEntity;
    }
}
