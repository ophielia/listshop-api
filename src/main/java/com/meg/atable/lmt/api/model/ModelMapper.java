package com.meg.atable.lmt.api.model;

import com.meg.atable.auth.api.model.User;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.*;

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
            //List<TAG> tags = toModel(dishEntity.getSlots());
            return new Dish(dishEntity.getId())
                    .description(dishEntity.getDescription())
                    .dishName(dishEntity.getDishName())
                    .tags(tags)
                    .lastAdded(dishEntity.getLastAdded())
                    .userId(dishEntity.getUserId());
        }
        return new Dish();
    }

    public static User toModel(UserEntity userEntity) {
        if (userEntity != null) {
            return new User(userEntity.getUsername(), userEntity.getEmail())
                    .creationDate(userEntity.getCreationDate());
        }
        return new User(null, null);
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
                .dish(toModel(dishEntity.getDish()))
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
        return toModel(cat, true);

    }

    public static ListLayoutCategory toModel(ListLayoutCategoryEntity cat, boolean includeTags) {
        if (cat == null) {
            return null;
        }
        ListLayoutCategory returnval = (ListLayoutCategory) new ListLayoutCategory(cat.getId())
                .name(cat.getName());
        returnval = (ListLayoutCategory) returnval.layoutId(cat.getLayoutId());
        if (includeTags) {
            returnval = (ListLayoutCategory) returnval.tags(toModel(cat.getTags()));
        } else {
            returnval = (ListLayoutCategory) returnval.tags(new ArrayList<>());
        }
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
            return result;
        }
        Set<String> uniqueDishSources = FlatStringUtils.inflateStringToSet(rawDishSources, ";");
        for (String dishId : uniqueDishSources) {
            if (dishSources.containsKey(Long.valueOf(dishId))) {
                DishEntity dish = dishSources.get(Long.valueOf(dishId));

                ItemSource source = toDishSourceModel( dish);
                result.add(source);
            }
        }
        return result;
    }

    private static ItemSource toDishSourceModel( DishEntity dish) {
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
                .toDelete(tagEntity.isToDelete())
                .searchSelect(tagEntity.getSearchSelect());
    }

    public static Tag toModel(TagExtendedEntity tagEntity) {
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
                .toDelete(tagEntity.getToDelete())
                .searchSelect(tagEntity.getSearchSelect());
    }

    public static MealPlan toModel(MealPlanEntity mealPlanEntity) {
        List<Slot> slots = slotsToModel(mealPlanEntity.getSlots());
        return new MealPlan(mealPlanEntity.getId())
                .name(mealPlanEntity.getName())
                .mealPlanType(mealPlanEntity.getMealPlanType() != null ? mealPlanEntity.getMealPlanType().name() : "")
                .userId(mealPlanEntity.getUserId().toString())
                .slots(slots);

    }

    public static Statistic toModel(ListTagStatistic statistic) {
        return new Statistic(statistic.getListTagStatId())
                .addedCount(statistic.getAddedCount())
                .addedToDish(statistic.getAddedToDishCount())
                .removedCount(statistic.getRemovedCount())
                .tagId(statistic.getTagId())
                .userId(statistic.getUserId());
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

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity, List<Category> itemCategories) {
        HashMap<Long, DishEntity> dishSourceDictionary = new HashMap<>();
        List<ItemSource> dishSources = new ArrayList<>();
        if (shoppingListEntity.getDishSources() != null) {
            shoppingListEntity.getDishSources().forEach(d -> {
                Long id = d.getId();
                dishSourceDictionary.put(id, d);
                dishSources.add(toDishSourceModel( d));

            });
        }
        List<ItemSource> listSources = new ArrayList<>();
        if (shoppingListEntity.getListSources() != null) {
            shoppingListEntity.getListSources().forEach(d -> listSources.add(toListSourceModel(d)));
        }

        List<Category> categories = itemCategoriesToModel(itemCategories, dishSourceDictionary);

        return new ShoppingList(shoppingListEntity.getId())
                .createdOn(shoppingListEntity.getCreatedOn())
                .listType(shoppingListEntity.getListType().name())
                .categories(categories)
                .dishSources(dishSources)
                .listSources(listSources)
                .layoutId(String.valueOf(shoppingListEntity.getListLayoutId()))
                .updated(shoppingListEntity.getLastUpdate())
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

    public static Item toModel(ItemEntity itemEntity) {
        return new Item(itemEntity.getId())
                .tag(toModel(itemEntity.getTag()))
                .listId(itemEntity.getListId().toString())
                .addedOn(itemEntity.getAddedOn())
                .updated(itemEntity.getUpdatedOn())
                .removed(itemEntity.getRemovedOn())
                .crossedOff(itemEntity.getCrossedOff())
                .usedCount(itemEntity.getUsedCount())
                .freeText(itemEntity.getFreeText());
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
        TargetType targetType=null;
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
        itemEntity.setListId(listId);
        itemEntity.setTagId(tagId);
        itemEntity.setAddedOn(input.getAddedOn());
        itemEntity.setUpdatedOn(input.getUpdated());
        itemEntity.setCrossedOff(input.getCrossedOff());
        itemEntity.setRemovedOn(input.getRemoved());
        itemEntity.setUsedCount(input.getUsedCount());
        return itemEntity;
    }

    public static ShoppingListEntity toEntity(ShoppingList shoppingList) {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity(shoppingList.getList_id());
        ListType listType = ListType.valueOf(shoppingList.getListType());
        shoppingListEntity.setListType(listType);
        shoppingListEntity.setUserId(shoppingList.getUserId());
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
        ListLayoutType layoutType = listLayout.getLayoutType()!=null?ListLayoutType.valueOf(listLayout.getLayoutType()):null;
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity();
        listLayoutEntity.setName(listLayout.getName());
        listLayoutEntity.setLayoutType(layoutType);
        return listLayoutEntity;
    }

    private static List<TagDrilldown> childrenTagsToModel(List<FatTag> childrenTags) {
        List<TagDrilldown> drilldownList = new ArrayList<>();
        if (childrenTags != null) {
            for (FatTag cat : childrenTags) {
                drilldownList.add(toModel(cat));
            }
        }
        return drilldownList;
    }

    public static TagDrilldown toModel(FatTag fatTag) {
        if (fatTag == null) {
            return null;
        }

        List<TagDrilldown> children = new ArrayList<>();
        if (fatTag.getChildren() != null) {
            children = childrenTagsToModel(fatTag.getChildren());
        }
        TagDrilldown result = new TagDrilldown(fatTag.getId());
        result.name(fatTag.getName())
                .description(fatTag.getDescription())
                .tagType(fatTag.getTagType().name())
                .power(fatTag.getPower())
                // don't need dishes in tags  .dishes(dishesToModel(tagEntity.getDishes()))
                .assignSelect(fatTag.getAssignSelect())
                .parentId(String.valueOf(fatTag.getParentId()))
                .searchSelect(fatTag.getSearchSelect());

        result.parentId(String.valueOf(fatTag.getParentId()));
        result.childrenList(children);

        return result;
    }

}
