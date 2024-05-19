/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.model;

import com.meg.listshop.admin.model.AdminUser;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.AdminUserDetailsEntity;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;
import com.meg.listshop.lmt.api.model.v2.Ingredient;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ModelMapper {
    private static final String DISH_PREFIX = "d";
    private static final String LIST_PREFIX = "l";
    private static final String SPECIAL_PREFIX = "s";

    private ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static ConversionGrid toConversionGridFromFactors(List<FoodConversionEntity> conversionFactors) {
        if (conversionFactors == null) {
            return null;
        }
        List<ConversionSample> samples = conversionFactors.stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList());
        ConversionGrid grid = new ConversionGrid();
        grid.setSamples(samples);
        return grid;
    }

    public static ConversionGrid toConversionGrid(List<ConversionSampleDTO> conversionSamples) {
        List<ConversionSample> samples = conversionSamples.stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList());
        ConversionGrid grid = new ConversionGrid();
        grid.setSamples(samples);
        return grid;
    }

    private static ConversionSample toModel(FoodConversionEntity factorEntity) {
        ConversionSample sample = new ConversionSample();
        sample.setFromUnit(String.valueOf(factorEntity.getUnitName()));
        sample.setFromAmount("1");
        sample.setToAmount(String.valueOf(factorEntity.getGramWeight()));
        sample.setToUnit("grams");
        return sample;
    }

    private static ConversionSample toModel(ConversionSampleDTO conversionSampleDTO) {
        ConversionSample sample = new ConversionSample();
        // assuming (for now, right or wrong) from => marker, to => unit size
        String fromUnit = conversionSampleDTO.getFromAmount().getUnit().getName();
        if (conversionSampleDTO.getFromAmount().getMarker() != null) {
            fromUnit = fromUnit + ", " + conversionSampleDTO.getFromAmount().getMarker();
        }
        String toUnit = conversionSampleDTO.getToAmount().getUnit().getName();
        if (conversionSampleDTO.getToAmount().getUnitSize() != null) {
            toUnit = toUnit + ", " + conversionSampleDTO.getToAmount().getUnitSize();
        }
        sample.setFromAmount(String.valueOf(conversionSampleDTO.getFromAmount().getQuantity()));
        sample.setToAmount(String.valueOf(conversionSampleDTO.getToAmount().getQuantity()));
        sample.setToUnit(toUnit);
        sample.setFromUnit(fromUnit);
        return sample;
    }

    public static Dish toModel(DishEntity dishEntity, boolean includeTags) {
        if (dishEntity != null) {
            List<Tag> dishTags = new ArrayList<>();
            if (includeTags) {
                dishTags = toModelItemsAsTags(dishEntity.getItems());
            }
            return new Dish(dishEntity.getId())
                    .description(dishEntity.getDescription())
                    .dishName(dishEntity.getDishName())
                    .reference(dishEntity.getReference())
                    .tags(dishTags)
                    .lastAdded(dishEntity.getLastAdded())
                    .userId(dishEntity.getUserId());
        }
        return new Dish();
    }

    public static Food toModel(FoodEntity foodEntity) {
        if (foodEntity == null) {
            return new Food();
        }
        Food suggestion = new Food();
        suggestion.setName(foodEntity.getName());
        suggestion.setId(String.valueOf(foodEntity.getFoodId()));
        suggestion.setCategoryId(String.valueOf(foodEntity.getCategoryId()));
        return suggestion;

    }

    public static Food toModel(FoodEntity foodEntity, List<FoodConversionEntity> factors) {
        ConversionGrid grid = toConversionGridFromFactors(factors);
        if (foodEntity == null) {
            return new Food();
        }
        Food suggestion = new Food();
        suggestion.setName(foodEntity.getName());
        suggestion.setId(String.valueOf(foodEntity.getFoodId()));
        suggestion.setCategoryId(String.valueOf(foodEntity.getCategoryId()));
        suggestion.setGrid(grid);
        return suggestion;

    }

    public static User toModel(UserEntity userEntity, String token) {
        if (userEntity != null) {
            String[] roles = toRoleListModel(userEntity.getAuthorities());
            return new User(userEntity.getUsername(), userEntity.getEmail())
                    .creationDate(userEntity.getCreationDate())
                    .token(token)
                    .userId(userEntity.getId())
                    .roles(roles);
        }
        return new User(null, null);
    }

    public static AdminUser toAdminModel(UserEntity userEntity) {
        if (userEntity != null) {
            return new AdminUser(userEntity.getEmail())
                    .created(userEntity.getCreationDate())
                    .userId(String.valueOf(userEntity.getId()))
                    .lastLogin(userEntity.getLastLogin());
        }
        return new AdminUser(null);
    }

    public static AdminUser toAdminModel(AdminUserDetailsEntity userEntity, List<UserPropertyEntity> properties) {
        List<UserProperty> propertyModelList = null;
        if (properties != null) {
            propertyModelList = properties.stream()
                    .map(ModelMapper::toModel)
                    .collect(Collectors.toList());

        }
        if (userEntity != null) {
            return new AdminUser(userEntity.getEmail())
                    .created(userEntity.getCreationDate())
                    .userId(String.valueOf(userEntity.getUserId()))
                    .lastLogin(userEntity.getLastLogin())
                    .listCount(userEntity.getListCount())
                    .dishCount(userEntity.getDishCount())
                    .mealPlanCount(userEntity.getMealPlanCount())
                    .userProperties(propertyModelList);
        }

        return new AdminUser(null);
    }

    private static String[] toRoleListModel(List<AuthorityEntity> authorities) {
        List<String> roleList = new ArrayList<>();
        if (authorities == null || authorities.isEmpty()) {
            return new String[0];
        }
        for (AuthorityEntity authority : authorities) {
            roleList.add(authority.getName().name());
        }
        return roleList.toArray(new String[0]);
    }

    public static Dish toModel(DishEntity dishEntity, List<TagEntity> tagEntities) {
        List<Tag> tags = toModel(tagEntities);
        return new Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .reference(dishEntity.getReference())
                .tags(tags)
                .lastAdded(dishEntity.getLastAdded())
                .userId(dishEntity.getUserId());
    }

    public static ListLayout toModel(ListLayoutEntity listLayoutEntity, List<ListLayoutCategoryPojo> listLayoutCategories) {
        List<ListLayoutCategory> categories = layoutCategoriesToModel(listLayoutCategories);

        return new ListLayout(listLayoutEntity.getId())
                .name(listLayoutEntity.getName())
                .categories(categories);
    }


    public static ListLayout toModel(ListLayoutEntity listLayoutEntity) {
        List<ListLayoutCategory> categories = layoutCategoryEntitiesToModel(listLayoutEntity.getCategories());
        String userId = listLayoutEntity.getUserId() != null ? String.valueOf(listLayoutEntity.getUserId()) : null;
        return new ListLayout(listLayoutEntity.getId())
                .name(listLayoutEntity.getName())
                .userId(userId)
                .isDefault(listLayoutEntity.getDefault())
                .categories(categories);
    }

    public static ListLayout toShortModel(ListLayoutEntity listLayoutEntity) {
        List<ListLayoutCategory> categories = layoutCategoryEntitiesToShortModel(listLayoutEntity.getCategories());
        String userId = listLayoutEntity.getUserId() != null ? String.valueOf(listLayoutEntity.getUserId()) : null;
        return new ListLayout(listLayoutEntity.getId())
                .name(listLayoutEntity.getName())
                .userId(userId)
                .isDefault(listLayoutEntity.getDefault())
                .categories(categories);
    }

    public static Target toModel(TargetEntity targetEntity) {
        List<TargetSlot> slots = targetSlotsToModel(targetEntity.getSlots());
        List<Tag> tags = toModel(targetEntity.getTargetTags());
        String targetType = targetEntity.getTargetType().name();
        return new Target(targetEntity.getTargetId())
                .userId(targetEntity.getUserId())
                .targetName(targetEntity.getTargetName())
                .slots(slots)
                .targetType(targetType)
                .proposalId(targetEntity.getProposalId())
                .created(targetEntity.getCreated())
                .lastUsed(targetEntity.getLastUsed())
                .targetTags(tags);

    }

    public static TargetSlot toModel(TargetSlotEntity targetSlotEntity) {
        List<Tag> tags = toModel(targetSlotEntity.getTags());

        return new TargetSlot(targetSlotEntity.getId())
                .targetId(targetSlotEntity.getTargetId())
                .slotDishTagId(targetSlotEntity.getSlotDishTagId())
                .slotDishTag(toModel(targetSlotEntity.getSlotDishTag()))
                .slotTags(tags)
                .slotOrder(targetSlotEntity.getSlotOrder());
    }

    public static TargetProposal toModel(ProposalEntity proposalEntity) {
        List<TargetProposalSlot> slots = proposalSlotsToModel(proposalEntity.getSlots());
        List<Tag> tags = toModel(proposalEntity.getTargetTags());
        return new TargetProposal(proposalEntity.getId())
                .userId(proposalEntity.getUserId())
                .targetName(proposalEntity.getTargetName())
                .created(proposalEntity.getCreated())
                .targetTags(tags)
                .canBeRefreshed(proposalEntity.isRefreshable())
                .proposalSlots(slots);
    }

    private static List<TargetProposalSlot> proposalSlotsToModel(List<ProposalSlotEntity> slotEntities) {
        List<TargetProposalSlot> targetSlots = new ArrayList<>();
        if (slotEntities != null) {
            for (ProposalSlotEntity slotEntity : slotEntities) {
                targetSlots.add(toModel(slotEntity));
            }
        }
        return targetSlots;
    }

    public static TargetProposalDish toModel(DishSlotEntity dishEntity) {
        List<Tag> tags = toModel(dishEntity.getMatchedTags());
        return new TargetProposalDish(dishEntity.getDishId())
                .dish(toModel(dishEntity.getDish(), false))
                .matchedTags(tags);
    }

    public static TargetProposalSlot toModel(ProposalSlotEntity slotEntity) {
        List<Tag> tags = toModel(slotEntity.getTags());
        List<TargetProposalDish> dishes = proposalDishSlotsToModel(slotEntity.getDishSlots());

        return new TargetProposalSlot(slotEntity.getId())
                .slotDishTag(toModel(slotEntity.getSlotDishTag()))
                .tags(tags)
                .selectedDishId(slotEntity.getPickedDishId())
                .selectedDishIndex(0)
                .dishSlotList(dishes)
                .slotOrder(slotEntity.getSlotNumber());

    }

    private static List<TargetSlot> targetSlotsToModel(List<TargetSlotEntity> targetSlotEntities) {
        List<TargetSlot> targetSlots = new ArrayList<>();
        if (targetSlotEntities != null) {
            for (TargetSlotEntity slotEntity : targetSlotEntities) {
                targetSlots.add(toModel(slotEntity));
            }
        }
        return targetSlots;
    }

    private static List<TargetProposalDish> proposalDishSlotsToModel(List<DishSlotEntity> slotDishEntities) {
        List<TargetProposalDish> dishSlots = new ArrayList<>();
        if (slotDishEntities != null) {
            for (DishSlotEntity dishEntity : slotDishEntities) {
                dishSlots.add(toModel(dishEntity));
            }
        }
        return dishSlots;
    }

    private static List<ListLayoutCategory> layoutCategoryEntitiesToModel(Set<ListLayoutCategoryEntity> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<ListLayoutCategory> categoryList = new ArrayList<>();
        for (ListLayoutCategoryEntity cat : categories) {
            ListLayoutCategory llc = new ListLayoutCategory(cat.getId());
            llc.setName(cat.getName());
            llc.setDisplayOrder(cat.getDisplayOrder());
            llc.setTags(toModel(cat.getTags()));
            categoryList.add(llc);
        }
        return categoryList;

    }

    private static List<ListLayoutCategory> layoutCategoryEntitiesToShortModel(Set<ListLayoutCategoryEntity> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<ListLayoutCategory> categoryList = new ArrayList<>();
        for (ListLayoutCategoryEntity cat : categories) {
            ListLayoutCategory llc = new ListLayoutCategory(cat.getId());
            llc.setName(cat.getName());
            llc.setDefault(cat.getDefault() != null && cat.getDefault());
            llc.setTags(toShortModel(cat.getTags()));
            llc.setDisplayOrder(cat.getDisplayOrder());
            categoryList.add(llc);
        }
        return categoryList;

    }

    private static List<ListLayoutCategory> layoutCategoriesToModel(List<ListLayoutCategoryPojo> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<ListLayoutCategory> categoryList = new ArrayList<>();
        for (ListLayoutCategoryPojo cat : categories) {
            ListLayoutCategory llc = new ListLayoutCategory(cat.getId());
            llc.name = cat.getName();
            llc.displayOrder = cat.getDisplayOrder();
            llc.setTags(toModel(cat.getTagEntities()));
            categoryList.add(llc);
        }
        return categoryList;

    }

    public static ListLayoutCategory toModel(ListLayoutCategoryPojo cat) {
        if (cat == null) {
            return null;
        }

        ListLayoutCategory layout = toModel(cat);
        // this is received pre-filled.  Just need to check for subcategories
        if (cat.getSubCategories() == null || cat.getSubCategories().isEmpty()) {
            return toModel(cat);
        }

        return layout;
    }

    public static Category toModel(ListLayoutCategoryEntity cat) {
        return toModel(cat, false);
    }

    public static Category toModel(ListLayoutCategoryEntity cat, boolean includeTags) {

        if (cat == null) {
            return null;
        }
        Category returnval = new Category(cat.getId())
                .name(cat.getName())
                .displayOrder(cat.getDisplayOrder())
                .categoryType(CategoryType.Standard.name());

        if (includeTags) {
            returnval = returnval.tags(toModel(cat.getTags()));
        }

        return returnval;

    }


    private static void enhanceSources(List<ShoppingListItem> items) {
        if (items == null) {
            return;
        }
        items.forEach(i -> {
            List<String> sources = toListItemSourceKeys(ModelMapper.DISH_PREFIX, i.getRawDishSources());
            sources.addAll(toListItemSourceKeys(ModelMapper.LIST_PREFIX, i.getRawListSources()));
            sources.addAll(toListItemSourceKeys(ModelMapper.SPECIAL_PREFIX, i.getHandles()));
            i.sourceKeys(sources);
        });
    }

    private static List<String> toListItemSourceKeys(String keyPrefix, Set<String> idSet) {
        List<String> sources = new ArrayList<>();

        for (String id : idSet) {
            String key = keyPrefix + id;
            sources.add(key);

        }
        return sources;
    }

    private static List<String> toListItemSourceKeys(String keyPrefix, String delimitedIdList) {
        if (delimitedIdList == null || delimitedIdList.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> idSet = FlatStringUtils.inflateStringToSet(delimitedIdList, ";");
        return toListItemSourceKeys(keyPrefix, idSet);
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

    private static List<Tag> toModelItemsAsTags(List<DishItemEntity> itemEntities) {
        if (itemEntities == null) {
            return new ArrayList<>();
        }
        return toModel(itemEntities.stream().map(DishItemEntity::getTag).collect(Collectors.toList()));
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

    private static List<Tag> toModel(Set<TagEntity> tagEntities) {
        List<Tag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toModel(entity));
        }
        return tags;
    }

    private static List<Tag> toShortModel(Set<TagEntity> tagEntities) {
        List<Tag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toShortModel(entity));
        }
        return tags;
    }


    public static Tag toModel(TagInfoDTO tagInfoDTO) {
        return new Tag(tagInfoDTO.getTagId())
                .name(tagInfoDTO.getName())
                .description(tagInfoDTO.getDescription())
                .userId(String.valueOf(tagInfoDTO.getUserId()))
                .tagType(tagInfoDTO.getTagType())
                .power(tagInfoDTO.getPower())
                .isGroup(tagInfoDTO.isGroup())
                .assignSelect(!tagInfoDTO.isGroup())
                .searchSelect(tagInfoDTO.isGroup())
                .parentId(String.valueOf(tagInfoDTO.getParentId()))
                .toDelete(tagInfoDTO.isToDelete());
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
                .toDelete(tagEntity.isToDelete());
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

    public static Tag itemToTagModel(DishItemEntity itemEntity) {
        if (itemEntity == null) {
            return null;
        }

        return new Tag(itemEntity.getTag().getId())
                .name(itemEntity.getTag().getName())
                .description(itemEntity.getTag().getDescription())
                .userId(String.valueOf(itemEntity.getTag().getUserId()))
                .tagType(itemEntity.getTag().getTagType().name())
                .power(itemEntity.getTag().getPower())
                .isGroup(itemEntity.getTag().getIsGroup())
                // don't need dishes in tags  .dishes(dishesToModel(tagEntity.getDishes()))
                .assignSelect(!itemEntity.getTag().getIsGroup())
                .searchSelect(itemEntity.getTag().getIsGroup())
                .parentId(String.valueOf(itemEntity.getTag().getParentId()))
                .toDelete(itemEntity.getTag().isToDelete());
    }

    public static Tag toShortModel(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }

        return new Tag(tagEntity.getId())
                .name(tagEntity.getName());
    }

    public static MealPlan toModel(MealPlanEntity mealPlanEntity, boolean includeSlots) {
        List<Slot> slots = new ArrayList<>();
        int slotCount = mealPlanEntity.getSlots().size();
        if (includeSlots) {
            slots = slotsToModel(mealPlanEntity.getSlots());
        }

        return new MealPlan(mealPlanEntity.getId())
                .name(mealPlanEntity.getName())
                .created(mealPlanEntity.getCreated())
                .mealPlanType(mealPlanEntity.getMealPlanType() != null ? mealPlanEntity.getMealPlanType().name() : "")
                .slotCount(slotCount)
                .userId(mealPlanEntity.getUserId().toString())
                .slots(slots);

    }

    public static List<Statistic> toModelListStatistic(List<ListTagStatistic> statisticEntities) {
        List<Statistic> statistics = new ArrayList<>();
        for (ListTagStatistic st : statisticEntities) {
            Statistic statistic = ModelMapper.toModel(st);
            statistics.add(statistic);
        }
        return statistics;
    }

    public static Statistic toModel(ListTagStatistic statistic) {
        return new Statistic(statistic.getListTagStatId())
                .addedCount(statistic.getAddedCount())
                .addedToDish(statistic.getAddedToDishCount())
                .removedCount(statistic.getRemovedCount())
                .tagId(statistic.getTagId())
                .userId(statistic.getUserId());
    }

    public static FoodCategoryMapping toModel(FoodMappingDTO foodMappingDTO) {
        FoodCategoryMapping mapping = new FoodCategoryMapping();
        mapping.setFoodCategoryId(String.valueOf(foodMappingDTO.getCategoryId()));
        mapping.setFoodCategoryName(foodMappingDTO.getCategoryName());
        mapping.setTagId(String.valueOf(foodMappingDTO.getTagId()));
        mapping.setTagName(foodMappingDTO.getTagName());

        return mapping;
    }

    private static Slot toModel(SlotEntity slotEntity) {
        return new Slot(slotEntity.getMealPlanSlotId())
                .mealPlanId(slotEntity.getMealPlan().getId())
                .dish(toModel(slotEntity.getDish(), false));
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

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity, List<ShoppingListCategory> itemCategories) {
        List<LegendSource> legendSources = new ArrayList<>();
        if (shoppingListEntity.getDishSources() != null &&
                !shoppingListEntity.getDishSources().isEmpty()) {
            Set<LegendSource> dishLegends = new HashSet<>();
            shoppingListEntity.getDishSources().forEach(d -> {
                String key = ModelMapper.DISH_PREFIX + d.getId();

                dishLegends.add(new LegendSource(key, d.getDishName()));
            });
            legendSources.addAll(dishLegends);
        }
        if (shoppingListEntity.getListSources() != null &&
                !shoppingListEntity.getListSources().isEmpty()) {
            Set<LegendSource> listLegends = new HashSet<>();
            shoppingListEntity.getListSources().forEach(d -> {
                String key = ModelMapper.LIST_PREFIX + d.getId();

                listLegends.add(new LegendSource(key, d.getName()));
            });
            legendSources.addAll(listLegends);
        }

        enhanceCategories(itemCategories);

        long itemCount = 0;
        if (shoppingListEntity.getItems() != null) {
            itemCount = shoppingListEntity.getItems().stream()
                    .filter(item -> item.getRemovedOn() == null && item.getCrossedOff() == null)
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


    private static void enhanceCategories(List<ShoppingListCategory> filledCategories
    ) {
        if (filledCategories == null) {
            return;
        }
        // go through list, converting items in categories to Items
        filledCategories.forEach(c -> enhanceSources(c.getItems()));
    }

    public static Item toModel(ListItemEntity listItemEntity) {
        return new Item(listItemEntity.getId())
                .tag(toModel(listItemEntity.getTag()))
                .listId(listItemEntity.getListId().toString())
                .addedOn(listItemEntity.getAddedOn())
                .updated(listItemEntity.getUpdatedOn())
                .removed(listItemEntity.getRemovedOn())
                .crossedOff(listItemEntity.getCrossedOff())
                .usedCount(listItemEntity.getUsedCount())
                .freeText(listItemEntity.getFreeText())
                .handles(listItemEntity.getHandles());
    }

    public static UserProperty toModel(UserPropertyEntity entity) {
        UserProperty userProperty = new UserProperty();
        userProperty.setKey(entity.getKey());
        userProperty.setValue(entity.getValue());
        return userProperty;
    }

    public static DishEntity toEntity(Dish dish) {
        if (dish == null) {
            return null;
        }
        Long dishId = dish.getId();
        DishEntity dishEntity = new DishEntity(dishId, dish.getDishName());

        dishEntity.setReference(dish.getReference());
        dishEntity.setUserId(dish.getUserId());
        dishEntity.setDescription(dish.getDescription());
        return dishEntity;
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        Long tagId = tag.getId() != null ? Long.valueOf(tag.getId()) : null;
        TagEntity tagEntity = new TagEntity(tagId);

        tagEntity.setName(tag.getName().trim());
        tagEntity.setDescription(tag.getDescription());
        if (tag.getTagType() != null) {
            tagEntity.setTagType(TagType.valueOf(tag.getTagType()));
        }
        tagEntity.setIsGroup(tag.getIsGroup());
        tagEntity.setPower(tag.getPower());

        return tagEntity;
    }

    public static TargetEntity toEntity(Target target) {
        if (target == null) {
            return null;
        }
        Long targetId = target.getTargetId();
        TargetType targetType = null;
        if (target.getTargetType() != null) {
            targetType = TargetType.valueOf(target.getTargetType());
        }
        TargetEntity targetEntity = new TargetEntity(targetId);

        targetEntity.setTargetName(target.getTargetName());
        targetEntity.setCreated(target.getCreated());
        targetEntity.setUserId(target.getUserId());
        targetEntity.setTargetType(targetType);
        targetEntity.setLastUsed(target.getLastUsed());
        return targetEntity;
    }


    public static TargetSlotEntity toEntity(TargetSlot targetSlot) {
        if (targetSlot == null) {
            return null;
        }
        TargetSlotEntity entity = new TargetSlotEntity(targetSlot.getTargetSlotId());
        entity.setSlotDishTagId(targetSlot.getSlotDishTagId());
        entity.setTargetId(targetSlot.getTargetId());
        entity.setSlotOrder(targetSlot.getSlotOrder());
        return entity;
    }

    public static MealPlanEntity toEntity(MealPlan mealPlan) {
        if (mealPlan == null) {
            return null;
        }
        Long mealPlanId = mealPlan.getMealPlanId();
        MealPlanEntity mealPlanEntity = new MealPlanEntity(mealPlanId);

        mealPlanEntity.setName(mealPlan.getName());
        mealPlanEntity.setMealPlanType(MealPlanType.valueOf(mealPlan.getMealPlanType()));
        if (mealPlan.getUserId() != null) {
            mealPlanEntity.setUserId(Long.valueOf(mealPlan.getUserId()));
        }

        return mealPlanEntity;
    }

    public static ListItemEntity toEntity(Item input) {
        Long id = input.getId() != null && input.getId() > 0 ?
                input.getId() : null;
        Long listId = input.getListId() != null && !input.getListId().equals("0") ?
                Long.valueOf(input.getListId()) : null;
        Long tagId = input.getTagId() != null ?
                Long.valueOf(input.getTagId()) : null;
        ListItemEntity listItemEntity = new ListItemEntity(id);
        listItemEntity.setTag(toEntity(input.getTag()));
        listItemEntity.setFreeText(input.getFreeText());
        listItemEntity.setListId(listId);
        listItemEntity.setTagId(tagId);
        listItemEntity.setAddedOn(input.getAddedOn());
        listItemEntity.setUpdatedOn(input.getUpdated());
        listItemEntity.setCrossedOff(input.getCrossedOff());
        listItemEntity.setRemovedOn(input.getRemoved());
        listItemEntity.setUsedCount(input.getUsedCount());
        return listItemEntity;
    }

    public static ShoppingListEntity toEntity(ShoppingListPut shoppingList) {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity(shoppingList.getList_id());

        // not setting items here, since items will be updated individually from client
        shoppingListEntity.setName(shoppingList.getName());
        shoppingListEntity.setIsStarterList(shoppingList.getStarterList());
        return shoppingListEntity;
    }

    public static UserPropertyEntity toEntity(UserProperty property) {
        UserPropertyEntity entity = new UserPropertyEntity();
        entity.setKey(property.getKey());
        entity.setValue(property.getValue());
        return entity;
    }


    public static FoodCategory toModel(FoodCategoryEntity foodCategoryEntity) {
        FoodCategory foodCategory = new FoodCategory();
        foodCategory.setCategoryName(foodCategoryEntity.getName());
        foodCategory.setCategoryId(String.valueOf(foodCategoryEntity.getId()));
        return foodCategory;
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
}
