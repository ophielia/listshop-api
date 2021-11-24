package com.meg.listshop.lmt.api.model;

import com.meg.listshop.auth.api.model.User;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.service.categories.ItemCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListShopCategory;

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

    public static Dish toModel(DishEntity dishEntity) {
        if (dishEntity != null) {
            List<Tag> tags = new ArrayList<>();
            return new Dish(dishEntity.getId())
                    .description(dishEntity.getDescription())
                    .dishName(dishEntity.getDishName())
                    .reference(dishEntity.getReference())
                    .tags(tags)
                    .lastAdded(dishEntity.getLastAdded())
                    .userId(dishEntity.getUserId());
        }
        return new Dish();
    }

    public static User toModel(UserEntity userEntity, String token) {
        if (userEntity != null) {
            String[] roles = toRoleListModel(userEntity.getAuthorities());
            return new User(userEntity.getUsername(), userEntity.getEmail())
                    .creationDate(userEntity.getCreationDate())
                    .token(token)
                    .roles(roles);
        }
        return new User(null, null);
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

    private static List<ListLayoutCategory> layoutCategoriesToModel(List<ListLayoutCategoryPojo> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<ListLayoutCategory> categoryList = new ArrayList<>();
        for (ListLayoutCategoryPojo cat : categories) {
            ListLayoutCategory llc = new ListLayoutCategory(cat.getId());
            llc.name = cat.getName();
            llc.displayOrder = String.valueOf(cat.getDisplayOrder());
            llc.setSubCategories(layoutSubCategoriesToModel(cat.getSubCategories()));
            llc.setTags(toModel(cat.getTagEntities()));
            categoryList.add(llc);
        }
        return categoryList;

    }

    private static List<ListLayoutCategory> layoutSubCategoriesToModel(List<ListShopCategory> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<ListLayoutCategory> categoryList = new ArrayList<>();
        for (ListShopCategory cat : categories) {
            ListLayoutCategory llc = new ListLayoutCategory(cat.getId());
            llc.name = cat.getName();
            llc.displayOrder = String.valueOf(cat.getDisplayOrder());
            llc.setSubCategories(layoutSubCategoriesToModel(cat.getSubCategories()));
            llc.setTags(toModel(((ListLayoutCategoryPojo) cat).getTagEntities()));
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

        List<ListLayoutCategoryPojo> subcategories = cat.getSubCategories().stream()
                .map(r -> (ListLayoutCategoryPojo) r)
                .collect(Collectors.toList());
        layout.setSubCategories(layoutCategoriesToModel(subcategories));
        return layout;
    }

    public static ListLayoutCategoryPojo toModel(ListLayoutCategoryEntity cat) {
        return toModel(cat, true);

    }

    public static ListLayoutCategoryPojo toModel(ListLayoutCategoryEntity cat, boolean includeTags) {
        if (cat == null) {
            return null;
        }
        ListLayoutCategoryPojo returnval = (ListLayoutCategoryPojo) new ListLayoutCategoryPojo(cat.getId())
                .name(cat.getName());
        returnval = (ListLayoutCategoryPojo) returnval.layoutId(cat.getLayoutId());
        if (includeTags) {
            returnval = (ListLayoutCategoryPojo) returnval.tags(toModel(cat.getTags()));
        } else {
            returnval = (ListLayoutCategoryPojo) returnval.tags(new ArrayList<>());
        }
        return returnval;

    }

    private static List<Item> simpleItemsToModel(List<ItemEntity> items) {
        List<Item> itemList = new ArrayList<>();
        if (items == null) {
            return itemList;
        }
        for (ItemEntity entity : items) {
            Item item = toModel(entity);
            List<String> sources = toListItemSourceKeys(ModelMapper.DISH_PREFIX, entity.getRawDishSources());
            sources.addAll(toListItemSourceKeys(ModelMapper.LIST_PREFIX, entity.getRawListSources()));
            sources.addAll(toListItemSourceKeys(ModelMapper.SPECIAL_PREFIX, entity.getHandles()));
            item.sourceKeys(sources);
            itemList.add(item);
        }
        return itemList;
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
                .created(mealPlanEntity.getCreated())
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

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity, List<ListShopCategory> itemCategories) {
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
        //MM?? open for now - how to know if item frequent exists?

        List<ShoppingListCategory> categories = itemCategoriesToModel(itemCategories);

        long itemCount = 0;
        if (shoppingListEntity.getItems() != null) {
            itemCount = shoppingListEntity.getItems().stream()
                    .filter(item -> item.getRemovedOn() == null && item.getCrossedOff() == null)
                    .count();
        }
        return new ShoppingList(shoppingListEntity.getId())
                .createdOn(shoppingListEntity.getCreatedOn())
                .categories(categories)
                .legendSources(legendSources)
                .isStarterList(shoppingListEntity.getIsStarterList())
                .name(shoppingListEntity.getName())
                .layoutId(String.valueOf(shoppingListEntity.getListLayoutId()))
                .updated(shoppingListEntity.getLastUpdate())
                .itemCount((int) itemCount)
                .userId(shoppingListEntity.getUserId());

    }

    private static List<ShoppingListCategory> itemCategoriesToModel(List<ListShopCategory> filledCategories
    ) {
        if (filledCategories == null) {
            return new ArrayList<>();
        }

        List<ShoppingListCategory> modelCategories = new ArrayList<>();

        // go through list, converting items in category and subcategories
        // (category is already in model - the server needed to organize, but
        // the item mapping is still handled here)
        for (ListShopCategory categoryModel : filledCategories) {
            ItemCategoryPojo cm = (ItemCategoryPojo) categoryModel;
            ShoppingListCategory model = new ShoppingListCategory(cm.getId());
            List<Item> items = new ArrayList<>();
            if (cm.getItemEntities() != null && !cm.getItemEntities().isEmpty()) {
                items = simpleItemsToModel(cm.getItemEntities());
            }
            model.setItems(items);

            // now - subcategories
            if (!categoryModel.getSubCategories().isEmpty()) {
                List<ShoppingListCategory> filledSubCats = itemCategoriesToModel(categoryModel.getSubCategories());
                model.setSubCategories(filledSubCats);
            }
            modelCategories.add(model);
        }
        return modelCategories;
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
                .freeText(itemEntity.getFreeText())
                .handles(itemEntity.getHandles());
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
        Long mealPlanId = mealPlan.getMealPlanId();
        MealPlanEntity mealPlanEntity = new MealPlanEntity(mealPlanId);

        mealPlanEntity.setName(mealPlan.getName());
        mealPlanEntity.setMealPlanType(MealPlanType.valueOf(mealPlan.getMealPlanType()));
        if (mealPlan.getUserId() != null) {
            mealPlanEntity.setUserId(Long.valueOf(mealPlan.getUserId()));
        }

        return mealPlanEntity;
    }

    public static ItemEntity toEntity(Item input) {
        Long id = input.getId() != null && input.getId() > 0 ?
                input.getId() : null;
        Long listId = input.getListId() != null && !input.getListId().equals("0") ?
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

    public static ShoppingListEntity toEntity(ShoppingListPut shoppingList) {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity(shoppingList.getList_id());

        // not setting items here, since items will be updated individually from client
        shoppingListEntity.setName(shoppingList.getName());
        shoppingListEntity.setIsStarterList(shoppingList.getStarterList());
        return shoppingListEntity;
    }

    public static ListLayoutCategoryEntity toEntity(ListLayoutCategory layoutCategory) {
        ListLayoutCategoryEntity categoryEntity = new ListLayoutCategoryEntity(layoutCategory.getId());
        categoryEntity.setName(layoutCategory.getName());
        categoryEntity.setLayoutId(layoutCategory.getId());
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


    // possibly unused
    public static ListTagStatistic toEntity(Statistic statistic) {
        if (statistic == null) {
            return null;
        }

        ListTagStatistic statEntity = new ListTagStatistic();
        statEntity.setListTagStatId(statistic.getListTagStatId());
        statEntity.setUserId(statistic.getUserId());
        statEntity.setTagId(statistic.getTagId());
        statEntity.setAddedToDishCount(statistic.getAddedToDish());
        statEntity.setRemovedCount(statistic.getRemovedCount());
        statEntity.setAddedCount(statistic.getAddedCount());

        return statEntity;
    }

}
