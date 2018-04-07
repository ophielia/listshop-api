package com.meg.atable.api.model;

import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.*;

import java.util.*;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ModelMapper {
    private ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static Dish toModel(DishEntity dishEntity) {
        if (dishEntity != null) {
            List<Tag> tags = new ArrayList<>();
            //List<Tag> tags = toModel(dishEntity.getSlots());
            return new Dish(dishEntity.getId())
                    .description(dishEntity.getDescription())
                    .dishName(dishEntity.getDishName())
                    .tags(tags)
                    .lastAdded(dishEntity.getLastAdded())
                    .userId(dishEntity.getUserId());
        }
        return null;
    }

    public static Dish toModel(DishEntity dishEntity, List<TagEntity> tagEntities) {
        List<Tag> tags = toModel(tagEntities);
        return new Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .tags(tags)
                .lastAdded(dishEntity.getLastAdded())
                .userId(dishEntity.getUserId());
    }

    public static ListLayout toModel(ListLayoutEntity listLayoutEntity, List<Category> tagCategories) {
        List<Category> categories = layoutCategoriesToModel(tagCategories);

        return new ListLayout(listLayoutEntity.getId())
                .name(listLayoutEntity.getName())
                .layoutType(listLayoutEntity.getLayoutType().name())
                .categories(categories);
    }


    public static Target toModel(TargetEntity targetEntity) {
        List<TargetSlot> slots = targetSlotsToModel(targetEntity.getSlots());
        List<Tag> tags = toModel(targetEntity.getTargetTags());
        return new Target(targetEntity.getTargetId())
                .userId(targetEntity.getUserId())
                .targetName(targetEntity.getTargetName())
                .slots(slots)
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

    public static TargetProposal toModel(TargetProposalEntity proposalEntity) {
        List<TargetProposalSlot> slots = targetProposalSlotsToModel(proposalEntity.getProposalSlots());
        List<Tag> tags = toModel(proposalEntity.getTargetTags());
        return new TargetProposal(proposalEntity.getProposalId())
                .userId(proposalEntity.getUserId())
                .targetName(proposalEntity.getTargetName())
                .created(proposalEntity.getCreated())
                .lastUsed(proposalEntity.getLastUsed())
                .targetTags(tags)
                .canBeRefreshed(proposalEntity.canBeRefreshed())
                .proposalSlots(slots);
    }

    private static List<TargetProposalSlot> targetProposalSlotsToModel(List<TargetProposalSlotEntity> slotEntities) {
        List<TargetProposalSlot> targetSlots = new ArrayList<>();
        if (slotEntities != null) {
            for (TargetProposalSlotEntity slotEntity : slotEntities) {
                targetSlots.add(toModel(slotEntity));
            }
        }
        return targetSlots;
    }

    public static TargetProposalDish toModel(TargetProposalDishEntity dishEntity) {
        List<Tag> tags = toModel(dishEntity.getMatchedTags());
        return new TargetProposalDish(dishEntity.getProposalDishId())
                .dish(toModel(dishEntity.getDish()))
                .matchedTags(tags);
    }

    public static TargetProposalSlot toModel(TargetProposalSlotEntity slotEntity) {
        List<Tag> tags = toModel(slotEntity.getTags());
        List<TargetProposalDish> dishes = proposalDishSlotsToModel(slotEntity.getDishSlotList());

        return new TargetProposalSlot(slotEntity.getSlotId())
                .slotDishTag(toModel(slotEntity.getSlotDishTag()))
                .tags(tags)
                .selectedDishId(slotEntity.getSelectedDishId())
                .selectedDishIndex(slotEntity.getSelectedDishIndex())
                .dishSlotList(dishes)
                .slotOrder(slotEntity.getSlotOrder());

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

    private static List<TargetProposalDish> proposalDishSlotsToModel(List<TargetProposalDishEntity> slotDishEntities) {
        List<TargetProposalDish> dishSlots = new ArrayList<>();
        if (slotDishEntities != null) {
            for (TargetProposalDishEntity dishEntity : slotDishEntities) {
                dishSlots.add(toModel(dishEntity));
            }
        }
        return dishSlots;
    }

    private static List<Category> layoutCategoriesToModel(List<Category> categories) {
        if (categories == null) {
            return categories;
        }

        List<Category> categoryList = new ArrayList<>();
        for (Category cat : categories) {
            ListLayoutCategory llc = (ListLayoutCategory) cat;
            List<Tag> tags = toModel(llc.getTagEntities());

            categoryList.add(toModel((ListLayoutCategory) llc.tags(tags)));
        }
        return categoryList;

    }

    public static ListLayoutCategory toModel(ListLayoutCategory cat) {
        if (cat == null) {
            return null;
        }

        // receive pre-filled.  Just need to check for subcategories;
        if (cat.getSubCategories() == null || cat.getSubCategories().isEmpty()) {
            return cat;
        }

        List<Category> subcategories = layoutCategoriesToModel(cat.getSubCategories());
        return (ListLayoutCategory) cat.subCategories(subcategories);
    }

    public static ListLayoutCategory toModel(ListLayoutCategoryEntity cat) {
        if (cat == null) {
            return null;
        }
        ListLayoutCategory returnval = (ListLayoutCategory) new ListLayoutCategory(cat.getId())
                .name(cat.getName());
        returnval = (ListLayoutCategory) returnval.layoutId(cat.getLayoutId());
        returnval = (ListLayoutCategory) returnval.tags(toModel(cat.getTags()));
        return returnval;

    }

    private static List<Item> simpleItemsToModel(List<ItemEntity> items, Map<Long, DishEntity> dishSources) {
        List<Item> itemList = new ArrayList<>();
        if (items == null) {
            return itemList;
        }
        for (ItemEntity entity : items) {
            Item item = toModel(entity);
            List<ItemSource> sources = toDishSourceModels(entity.getRawDishSources(), dishSources);
            item.setDishSources(sources);
            List<ItemSource> listSources = toListSourceModels(entity.getRawListSources());
            item.setListSources(listSources);
            itemList.add(item);
        }
        return itemList;
    }

    private static List<ItemSource> toListSourceModels(String rawItemSources) {
        List<ItemSource> result = new ArrayList<>();
        if (rawItemSources == null) {
            return result;
        }
        Set<String> uniqueSources = FlatStringUtils.inflateStringToSet(rawItemSources, ";");
        for (String listsource : uniqueSources) {
            ItemSource source = new ItemSource();
            source.setDisplay(listsource);
            source.setType("List");
            result.add(source);
        }
        return result;
    }

    private static ItemSource toListSourceModel(String listsource) {
        ItemSource source = new ItemSource();
        source.setDisplay(listsource);
        source.setType("List");

        return source;
    }

    private static List<ItemSource> toDishSourceModels(String rawDishSources, Map<Long, DishEntity> dishSources) {
        List<ItemSource> result = new ArrayList<>();
        if (rawDishSources == null) {
            return  result;
        }
        Set<String> uniqueDishSources = FlatStringUtils.inflateStringToSet(rawDishSources, ";");
        for (String dishId : uniqueDishSources) {
            if (dishSources.containsKey(Long.valueOf(dishId))) {
                DishEntity dish = dishSources.get(Long.valueOf(dishId));

                ItemSource source = toDishSourceModel(dishId, dish);
                result.add(source);
            }
        }
        return result;
    }



    private static ItemSource toDishSourceModel(String dishId, DishEntity dish) {
            ItemSource source = new ItemSource();
            source.setDisplay(dish.getDishName());
            source.setId(dish.getId());
            source.setType("Dish");
            return source;
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
                .tagType(tagEntity.getTagType().name())
                .power(tagEntity.getPower())
                // don't need dishes in tags  .dishes(dishesToModel(tagEntity.getDishes()))
                .assignSelect(tagEntity.getAssignSelect())
                .parentId(String.valueOf(tagEntity.getParentId()))
                .searchSelect(tagEntity.getSearchSelect())
                .ratingFamily(tagEntity.getRatingFamily());
    }


    public static TagExtended toExtendedModel(TagEntity tagEntity) {
        return new TagExtended(tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getDescription(),
                tagEntity.getTagType(),
                tagEntity.getRatingFamily(),
                tagEntity.getParentId(),
                tagEntity.getChildrenIds(),
                tagEntity.getSearchSelect(),
                tagEntity.getAssignSelect());
    }

    public static MealPlan toModel(MealPlanEntity mealPlanEntity) {
        List<Slot> slots = slotsToModel(mealPlanEntity.getSlots());
        return new MealPlan(mealPlanEntity.getId())
                .name(mealPlanEntity.getName())
                .mealPlanType(mealPlanEntity.getMealPlanType().name())
                .userId(mealPlanEntity.getUserId().toString())
                .slots(slots);

    }

    private static Slot toModel(SlotEntity slotEntity) {
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

    private static List<Dish> dishesToModel(List<DishEntity> dishes) {
        List<Dish> dishList = new ArrayList<>();
        if (dishes == null) {
            return dishList;
        }
        for (DishEntity entity : dishes) {
            dishList.add(toModel(entity));
        }
        return dishList;
    }

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity, List<Category> itemCategories) {
        HashMap<Long, DishEntity> dishSourceDictionary = new HashMap<>();
        List<ItemSource> dishSources = new ArrayList<>();
        if (shoppingListEntity.getDishSources() != null) {
            shoppingListEntity.getDishSources().forEach(d -> {
                Long id = d.getId();
                dishSourceDictionary.put(id, d);
                dishSources.add(toDishSourceModel(String.valueOf(id), d));

            });
        }
        List<ItemSource> listSources = new ArrayList<>();
        if (shoppingListEntity.getListSources() != null) {
            shoppingListEntity.getListSources().forEach(d -> {
                listSources.add(toListSourceModel( d));

            });
        }

        List<Category> categories = itemCategoriesToModel(itemCategories, dishSourceDictionary);
        String layoutType = shoppingListEntity.getListLayoutType() != null ? shoppingListEntity.getListLayoutType().name() : "";

        return new ShoppingList(shoppingListEntity.getId())
                .createdOn(shoppingListEntity.getCreatedOn())
                .listType(shoppingListEntity.getListType().name())
                .layoutType(layoutType)
                .categories(categories)
                .dishSources(dishSources)
                .listSources(listSources)
                .itemCount(shoppingListEntity.getItems() != null ? shoppingListEntity.getItems().size() : 0)
                .userId(shoppingListEntity.getUserId());

    }


    private static List<Category> itemCategoriesToModel(List<Category> filledCategories, Map<Long, DishEntity> dishSources) {
        if (filledCategories == null) {
            return filledCategories;
        }

        // go through list, converting items in category and subcategories
        // (category is already in model - the server needed to organize, but
        // the item mapping is still handled here)
        for (Category categoryModel : filledCategories) {
            ItemCategory cm = (ItemCategory) categoryModel;
            List<Item> items = new ArrayList<>();
            if (cm.getItemEntities() != null && !cm.getItemEntities().isEmpty()) {
                items = simpleItemsToModel(cm.getItemEntities(), dishSources);
            }
            cm.items(items);
            cm.itemEntities(null);

            // now - subcategories
            if (!categoryModel.getSubCategories().isEmpty()) {
                List<Category> filledSubCats = itemCategoriesToModel(categoryModel.getSubCategories(), dishSources);
                categoryModel.subCategories(filledSubCats);
            }
        }
        return filledCategories;
    }


    private static Item toModel(ItemEntity itemEntity) {
        return new Item(itemEntity.getId())
                .tag(toModel(itemEntity.getTag()))
                .itemSource(itemEntity.getRawDishSources())
                .listId(itemEntity.getListId().toString())
                .addedOn(itemEntity.getAddedOn())
                .crossedOff(itemEntity.getCrossedOff())
                .usedCount(itemEntity.getUsedCount())
                .freeText(itemEntity.getFreeText())
                .listCategory(itemEntity.getListCategory());
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        Long tagId = tag.getId() != null ? new Long(tag.getId()) : null;
        TagEntity tagEntity = new TagEntity(tagId);

        tagEntity.setName(tag.getName().trim());
        tagEntity.setDescription(tag.getDescription());
        tagEntity.setTagType(TagType.valueOf(tag.getTagType()));
        tagEntity.setRatingFamily(tag.getRatingFamily());
        tagEntity.setSearchSelect(tag.getSearchSelect());
        tagEntity.setAssignSelect(tag.getAssignSelect());
        tagEntity.setPower(tag.getPower());

        return tagEntity;
    }


    public static TargetEntity toEntity(Target target) {
        if (target == null) {
            return null;
        }
        Long targetId = target.getTargetId();
        TargetEntity targetEntity = new TargetEntity(targetId);

        targetEntity.setTargetName(target.getTargetName());
        targetEntity.setCreated(target.getCreated());
        targetEntity.setUserId(target.getUserId());
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
        Long mealPlanId = mealPlan != null && mealPlan.getMealPlanId() != null ? mealPlan.getMealPlanId() : null;
        MealPlanEntity mealPlanEntity = new MealPlanEntity(mealPlanId);

        mealPlanEntity.setName(mealPlan.getName());
        mealPlanEntity.setMealPlanType(MealPlanType.valueOf(mealPlan.getMealPlanType()));
        if (mealPlan.getUserId() != null) {
            mealPlanEntity.setUserId(new Long(mealPlan.getUserId()));
        }

        return mealPlanEntity;
    }

    public static ItemEntity toEntity(Item input) {
        Long id = input.getId();
        Long listId = input.getListId() != null ?
                Long.valueOf(input.getListId()) : null;
        Long tagId = input.getTagId() != null ?
                Long.valueOf(input.getTagId()) : null;
        ItemEntity itemEntity = new ItemEntity(id);
        itemEntity.setTag(toEntity(input.getTag()));
        itemEntity.setFreeText(input.getFreeText());
        itemEntity.setRawDishSources(input.getItemSource());
        itemEntity.setListId(listId);
        itemEntity.setTagId(tagId);
        return itemEntity;
    }

    public static ShoppingListEntity toEntity(ShoppingList shoppingList) {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity(shoppingList.getList_id());
        ListType listType = ListType.valueOf(shoppingList.getListType());
        shoppingListEntity.setListType(listType);
        shoppingListEntity.setUserId(shoppingList.getUserId());
        ListLayoutType layoutType = ListLayoutType.valueOf(shoppingList.getLayoutType());
        shoppingListEntity.setListLayoutType(layoutType);
        // not setting items here, since items will be updated individually from client
        return shoppingListEntity;
    }

    public static ListLayoutCategoryEntity toEntity(ListLayoutCategory layoutCategory) {
        ListLayoutCategoryEntity categoryEntity = new ListLayoutCategoryEntity(layoutCategory.getId());
        categoryEntity.setName(layoutCategory.getName());
        categoryEntity.setLayoutId(layoutCategory.getLayoutId());
        // not setting tags from here
        return categoryEntity;
    }

    public static ListLayoutEntity toEntity(ListLayout listLayout) {
        ListLayoutType layoutType = ListLayoutType.valueOf(listLayout.getLayoutType());
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity(listLayout.getLayoutId());
        listLayoutEntity.setLayoutType(layoutType);
        listLayoutEntity.setName(listLayout.getName());
        return listLayoutEntity;
    }


    private static List<ListLayoutCategory> categoriesToModel(List<ListLayoutCategoryEntity> categories) {
        List<ListLayoutCategory> categoryList = new ArrayList<>();
        if (categories != null) {
            for (ListLayoutCategoryEntity cat : categories) {
                categoryList.add(toModel(cat));
            }
        }
        return categoryList;
    }


}
