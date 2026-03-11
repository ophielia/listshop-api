/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.api.model.v2;

//import com.meg.listshop.lmt.api.model.*;
//import com.meg.listshop.lmt.api.model.*;
//import com.meg.listshop.lmt.api.model.LegendSource;

import com.meg.listshop.common.FractionUtils;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.Suggestion;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class V2ModelMapper {
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
            amount = extractAmount(ingredientDto);
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

    private static Amount extractAmount(DishItemDTO ingredientDto) {
        return new Amount()
                .withFractionalQuantity(fractionTypeToName(ingredientDto.getFractionalQuantity()))
                .withWholeQuantity(ingredientDto.getWholeQuantity())
                .withQuantity(ingredientDto.getQuantity())
                .withRoundedQuantity(ingredientDto.getQuantity())
                .withUnitDisplay(ingredientDto.getUnitName())
                .withUnitId(String.valueOf(ingredientDto.getUnitId()))
                .withQuantityDisplay(ingredientDto.getQuantityDisplay())
                .withModifiers(ingredientDto.getRawModifiers())
                .withDisplay(ingredientDto.getRawEntry());
    }

    private static Amount extractAmount(ListItemDetailEntity detailEntity, Map<Long, String> unitMap) {
        String unitDisplay = unitMap.get(detailEntity.getUnitId());
        String quantityDisplay = FractionUtils.getQuantityDisplay(detailEntity.getWholeQuantity(), detailEntity.getFractionalQuantity());
        return new Amount()
                .withFractionalQuantity(fractionTypeToName(detailEntity.getFractionalQuantity()))
                .withWholeQuantity(detailEntity.getWholeQuantity())
                .withQuantity(detailEntity.getQuantity())
                .withRoundedQuantity(detailEntity.getQuantity())
                .withUnitDisplay(unitDisplay)
                .withUnitId(String.valueOf(detailEntity.getUnitId()))
                .withQuantityDisplay(quantityDisplay)
                .withModifiers(null)
                .withDisplay(detailEntity.getRawEntry());
    }

    private static Amount extractAmount(ListItemEntity itemEntity, Map<Long, String> unitMap) {
        String unitDisplay = unitMap.get(itemEntity.getUnitId());
        return new Amount()
                .withFractionalQuantity(fractionTypeToName(itemEntity.getFractionalQuantity()))
                .withWholeQuantity(itemEntity.getWholeQuantity())
                .withQuantity(itemEntity.getRawQuantity())
                .withRoundedQuantity(itemEntity.getRoundedQuantity())
                .withUnitDisplay(unitDisplay)
                .withUnitId(String.valueOf(itemEntity.getUnitId()))
                .withQuantityDisplay(itemEntity.getAmountText())
                .withModifiers(null)
                .withDisplay(itemEntity.getDisplay());
    }

    private static String fractionTypeToName(FractionType fractionType) {
        if (fractionType == null) {
            return null;
        }
        return fractionType.name();
    }

    public static NestedShoppingList toNestedListModel(ShoppingListDTO dto) {
        return new NestedShoppingList(dto.getListId())
                .withName(dto.getName())
                .withCreatedOn(dto.getCreatedOn())
                .withUpdated(dto.getLastUpdate())
                .withItemCount(dto.getItemCount())
                .withUserId(String.valueOf(dto.getUserId()))
                .withIsStarterList(dto.isStarterList());
    }

    public static ShoppingList toModel(ShoppingListDTO listDTO) {
        // unit map
        Map<Long, String> unitMap = listDTO.getUnitMapping();
        // prepare categories
        List<ShoppingListCategory> listCategories = toCategoryList(listDTO.getCategories(), unitMap);
        // prepare sources
        List<LegendSource> legendSources = toLegendSourceList(listDTO.getSources());
        // create shopping list and return
        return new ShoppingList(listDTO.getListId())
                .withName(listDTO.getName())
                .withCreatedOn(listDTO.getCreatedOn())
                .withUpdated(listDTO.getLastUpdate())
                .withLayoutId(String.valueOf(listDTO.getLayoutId()))
                .withIsStarterList(listDTO.isStarterList())
                .withCategories(listCategories)
                .withLegendSources(legendSources)
                .withItemCount(listDTO.getItemCount());
    }

    private static List<ShoppingListCategory> toCategoryList(List<CategoryDTO> categories, Map<Long, String> unitMap) {
        List<ShoppingListCategory> categoryModels = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            return categoryModels;
        }
        for (CategoryDTO categoryDTO : categories) {
            categoryModels.add(toModel(categoryDTO, unitMap));
        }
        return categoryModels;
    }

    private static List<LegendSource> toLegendSourceList(List<SourceDTO> sources) {
        List<LegendSource> legendSources = new ArrayList<>();
        if (sources == null || sources.isEmpty()) {
            return legendSources;
        }
        for (SourceDTO sourceDTO : sources) {
            legendSources.add(toModel(sourceDTO));
        }
        return legendSources;
    }

    private static ShoppingListCategory toModel(CategoryDTO categoryDTO, Map<Long, String> unitMap) {
        List<ShoppingListItem> listCategories = toItemList(categoryDTO.getItems(), unitMap);
        return new ShoppingListCategory(String.valueOf(categoryDTO.getCategoryId()),
                categoryDTO.getName(),
                categoryDTO.getDisplayOrder(),
                listCategories);
    }

    private static List<ShoppingListItem> toItemList(List<ListItemEntity> listItems, Map<Long, String> unitMap) {
        List<ShoppingListItem> itemModels = new ArrayList<>();
        if (listItems == null || listItems.isEmpty()) {
            return itemModels;
        }
        for (ListItemEntity listItem : listItems) {
            itemModels.add(toModel(listItem, unitMap));
        }
        return itemModels;
    }

    public static LegendSource toModel(SourceDTO sourceDTO) {
        return new LegendSource(sourceDTO.getReferenceId(), sourceDTO.getReferenceType().name(), sourceDTO.getName());
    }

    public static ShoppingListItem toModel(ListItemEntity listItemEntity, Map<Long, String> unitMap) {
        List<ShoppingListItemDetails> itemDetails = toModelList(listItemEntity.getDetails(), unitMap);
        Amount amount = extractAmount(listItemEntity, unitMap);
        NestedTag tag = new NestedTag(listItemEntity.getTag().getId(), listItemEntity.getTag().getName());
        return new ShoppingListItem(listItemEntity.getId())
                .withTag(tag)
                .withAmount(amount)
                .withDetails(itemDetails)
                .withListId(listItemEntity.getListId().toString())
                .withAddedOn(listItemEntity.getAddedOn())
                .withUpdated(listItemEntity.getUpdatedOn())
                .withRemoved(listItemEntity.getRemovedOn())
                .withCrossedOff(listItemEntity.getCrossedOff())
                .withUsedCount(listItemEntity.getUsedCount());
    }

    private static List<ShoppingListItemDetails> toModelList(List<ListItemDetailEntity> detailEntities, Map<Long, String> unitMap) {
        List<ShoppingListItemDetails> itemModels = new ArrayList<>();
        if (detailEntities == null || detailEntities.isEmpty()) {
            return itemModels;
        }
        for (ListItemDetailEntity itemDetail : detailEntities) {
            itemModels.add(toModel(itemDetail, unitMap));
        }
        return itemModels;
    }

    public static ShoppingListItemDetails toModel(ListItemDetailEntity detailEntity, Map<Long, String> unitMap) {
        Amount amount = extractAmount(detailEntity, unitMap);
        return new ShoppingListItemDetails()
                .withItemDetailId(String.valueOf(detailEntity.getItem().getId()))
                .withDishId(String.valueOf(detailEntity.getLinkedDishId()))
                .withListId(String.valueOf(detailEntity.getLinkedListId()))
                .withAmount(amount);
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


    public static ShoppingListDTO toDto(ShoppingListPut shoppingList) {
        return new ShoppingListDTO(shoppingList.getListId(),
                shoppingList.getName(),
                null,
                null,
                null,
                shoppingList.getStarterList(),
                0);
    }
}
