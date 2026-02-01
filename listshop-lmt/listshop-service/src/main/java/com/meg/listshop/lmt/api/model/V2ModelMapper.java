/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.model;

import com.meg.listshop.lmt.api.model.v2.Ingredient;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.pojos.SuggestionDTO;

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

        Ingredient ingredient = new Ingredient();
        ingredient.setId(String.valueOf(ingredientDto.getDishItemId()));
        ingredient.setTagId(String.valueOf(ingredientDto.getTagId()));
        ingredient.setTagDisplay(ingredientDto.getTagDisplay());
        ingredient.setWholeQuantity(ingredientDto.getWholeQuantity());
        if (ingredientDto.getFractionalQuantity() != null) {
            ingredient.setFractionalQuantity(ingredientDto.getFractionalQuantity().name());
        }
        ingredient.setUnitId(String.valueOf(ingredientDto.getUnitId()));
        ingredient.setUnitName(ingredientDto.getUnitName());
        ingredient.setRawModifiers(ingredientDto.getRawModifiers());
        ingredient.setRawEntry(ingredientDto.getRawEntry());
        ingredient.setUnitDisplay(ingredientDto.getUnitDisplay());
        String quantityDisplay = "";
        if (ingredientDto.getWholeQuantity() != null) {
            quantityDisplay = quantityDisplay + ingredientDto.getWholeQuantity();
        }
        if (ingredientDto.getFractionalQuantity() != null) {
            quantityDisplay = quantityDisplay + " " + ingredientDto.getFractionalQuantity().getDisplayName();
        }
        ingredient.setQuantityDisplay(quantityDisplay);
        return ingredient;
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

    public static com.meg.listshop.lmt.api.model.v2.Dish toModel(DishDTO dishDto, boolean includeTags) {
        // tags
        List<Tag> dishTags = new ArrayList<>();
        if (includeTags) {
            dishTags = toModelItemsAsTags(dishDto.getTags());
        }
        // ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        if (includeTags) {
            ingredients = toModelIngredients(dishDto.getIngredients());
        }

        return new com.meg.listshop.lmt.api.model.v2.Dish(dishDto.getDish().getId())
                .description(dishDto.getDish().getDescription())
                .dishName(dishDto.getDish().getDishName())
                .reference(dishDto.getDish().getReference())
                .tags(dishTags)
                .ratings(dishDto.getRatings())
                .ingredients(ingredients)
                .lastAdded(dishDto.getDish().getLastAdded())
                .userId(dishDto.getDish().getUserId());
    }

    public static com.meg.listshop.lmt.api.model.v2.Dish toV2DishModel(DishEntity dishEntity) {
        // tags and ingredients - to do....

        return new com.meg.listshop.lmt.api.model.v2.Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .reference(dishEntity.getReference())
                .lastAdded(dishEntity.getLastAdded())
                .userId(dishEntity.getUserId());
    }

    private static void enhanceSources(List<ShoppingListItem> items) {
        if (items == null) {
            return;
        }

        items.forEach(i -> {
            List<String> sourceList = i.getSources().stream()
                    .map(s -> toV1SourceTags(s))
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

    private static List<Ingredient> toModelIngredients(List<DishItemDTO> ingredientDTOs) {
        if (ingredientDTOs == null) {
            return new ArrayList<>();
        }
        List<Ingredient> ingredients = new ArrayList<>();
        for (DishItemDTO dishItemDTO : ingredientDTOs) {
            ingredients.add(toModel(dishItemDTO));
        }
        return ingredients;
    }

    private static void enhanceCategories(List<ShoppingListCategory> filledCategories
    ) {
        if (filledCategories == null) {
            return;
        }
        // go through list, converting items in categories to Items
        filledCategories.forEach(c -> enhanceSources(c.getItems()));
    }

    private static List<Tag> toModelItemsAsTags(List<DishItemEntity> itemEntities) {
        if (itemEntities == null) {
            return new ArrayList<>();
        }
        return toModel(itemEntities.stream().map(DishItemEntity::getTag).toList());
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
        if (tagEntity == null) {
            return null;
        }

        return new Tag(tagEntity.getId())
                .name(tagEntity.getName())
                .description(tagEntity.getDescription())
                .userId(String.valueOf(tagEntity.getUserId()))
                .tagType(tagEntity.getTagType().name())
                .power(tagEntity.getPower())
                .isGroup(tagEntity.getIsGroup())
                // don't need dishes in tags  .dishes(dishesToModel(tagEntity.getDishes()))
                .assignSelect(!tagEntity.getIsGroup())
                .searchSelect(tagEntity.getIsGroup())
                .parentId(String.valueOf(tagEntity.getParentId()))
                .isLiquid(tagEntity.getIsLiquid())
                .toDelete(tagEntity.isToDelete());
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

}
