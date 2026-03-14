/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.v2.impl;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.v2.MergeRequest;
import com.meg.listshop.lmt.api.model.v2.MergeResult;
import com.meg.listshop.lmt.api.model.v2.SourceReferenceType;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.pojos.*;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.BaseShoppingListService;
import com.meg.listshop.lmt.list.LegacyShoppingListService;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.list.v2.ShoppingListService;
import com.meg.listshop.lmt.service.LayoutService;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional(rollbackFor = ItemProcessingException.class)
public class ShoppingListServiceImpl extends BaseShoppingListService implements ShoppingListService {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingListServiceImpl.class);

    @Autowired
    public ShoppingListServiceImpl(TagService tagService,
                                   DishService dishService,
                                   ShoppingListRepository shoppingListRepository,
                                   LayoutService listLayoutService,
                                   MealPlanService mealPlanService,
                                   ItemRepository itemRepository,
                                   ItemChangeRepository itemChangeRepository,
                                   ListTagStatisticService listTagStatisticService,
                                   ListItemStateMachine listItemStateMachine) {
        super(tagService, dishService, shoppingListRepository, listLayoutService,
                mealPlanService, itemRepository, itemChangeRepository, listTagStatisticService, listItemStateMachine);
    }

    @Override
    public MergeResult mergeFromClient(Long userId, MergeRequest mergeRequest) {
        return null;
    }

    public List<CategoryDTO> retrieveListCategories(Long id) {
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findById(id).orElse(null);

        if (shoppingListEntity == null) {
            return new ArrayList<>();
        }
        Long userLayoutId = determineUserLayout(shoppingListEntity.getUserId(), shoppingListEntity.getListLayoutId());

        // find frequently crossed off
        List<Long> frequentTagIds = listTagStatisticService.findFrequentIdsForList(shoppingListEntity.getId(), shoppingListEntity.getUserId());
        // gather mapping information
        Map<String, CategoryDTO> categoryMap = retrieveCategoriesForListAndLayout(userLayoutId, shoppingListEntity.getId());
        Map<Long, String> tagToCategory = retrieveItemToCategoryMapping(shoppingListEntity.getId(), userLayoutId).stream()
                .collect(Collectors.toMap(ItemToCategoryDTO::tagId, ItemToCategoryDTO::categoryName));

        // then we need to retrieve filled ListItems for list - just filler here - may need more "energetic" in terms of entity graph
        List<ListItemEntity> listItems = itemRepository.findFilledObjectsByListId(shoppingListEntity.getId());
        for (ListItemEntity listItem : listItems) {
            if (listItem.getRemovedOn() != null) {
                continue;
            }
            Long tagId = listItem.getTag().getId();
            // get source list for listItem (looking at details, frequent)
            Set<String> sourceStrings = extractSourceDTO(listItem);
            if (frequentTagIds.contains(tagId)) {
                sourceStrings.add(LegacyShoppingListService.FREQUENT);
            }
            // map to ListItemDTO
            ListItemDTO listItemDTO = new ListItemDTO(listItem, sourceStrings);
            // get category name from category map
            String categoryName = tagToCategory.get(tagId);
            // add ListItemDTO to items in category
            if (categoryName == null) {
                continue;
            }
            categoryMap.get(categoryName).getItems().add(listItemDTO);
        }
        // sort items in category by lower tag name
        categoryMap.values().forEach(category -> {
            category.getItems().sort(Comparator.comparing(item -> item.getTag().getName().toLowerCase()));
        });
        // sort categories by display order
        List<CategoryDTO> result = new ArrayList<>(categoryMap.values());
        result.sort(Comparator.comparing(CategoryDTO::getDisplayOrder));
        // return list of categories
        return result;
    }

    private Set<String> extractSourceDTO(ListItemEntity listItem) {
        List<SourceDTO> sources = new ArrayList<>();
        listItem.getDetails().stream()
                .filter(detail -> detail.getLinkedDishId() != null)
                .forEach(detail -> sources.add(new SourceDTO(detail.getLinkedDishId(), null, SourceReferenceType.DISH.name())));
        listItem.getDetails().stream()
                .filter(detail -> detail.getLinkedListId() != null && !detail.getLinkedListId().equals(listItem.getListId()))
                .forEach(detail -> sources.add(new SourceDTO(detail.getLinkedListId(), null, SourceReferenceType.LIST.name())));
        return sources.stream()
                .map(s -> s.getReferenceType().name() + s.getReferenceId())
                .collect(Collectors.toSet());
    }

    private List<ItemToCategoryDTO> retrieveItemToCategoryMapping(Long listId, Long userLayoutId) {
        List<ItemToCategoryDTO> mappings = new ArrayList<>();
        mappings.addAll(itemRepository.getUserItemToCategoryMapping(userLayoutId, listId));
        Set<Long> tagIds = mappings.stream().map(ItemToCategoryDTO::tagId).collect(Collectors.toSet());
        itemRepository.getStandardItemToCategoryMapping(listId).stream()
                .filter(mapping -> !tagIds.contains(mapping.tagId()))
                .forEach(mapping -> mappings.add(mapping));
        return mappings;
    }

    private Map<String, CategoryDTO> retrieveCategoriesForListAndLayout(Long userLayoutId, Long listId) {
        // from repository, get distinct list_categories for all items in list
        Map<String, CategoryDTO> categoryMap = getUserCategoriesForList(userLayoutId, listId);
        List<ListLayoutCategoryEntity> standardCategories = listLayoutService.getStandardCategoriesForList(listId);
        standardCategories.stream()
                .filter(category -> !categoryMap.containsKey(toTrimmedLower(category.getName())))
                .forEach(category -> categoryMap.put(toTrimmedLower(category.getName()),
                        new CategoryDTO(category.getId(), category.getName(), category.getDisplayOrder())));
        return categoryMap;
    }

    private String toTrimmedLower(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase();
    }

    private Map<String, CategoryDTO> getUserCategoriesForList(Long userLayoutId, Long listId) {
        List<ListLayoutCategoryEntity> layoutCategories = listLayoutService.getUserCategoriesForList(userLayoutId, listId);
        if (layoutCategories == null || layoutCategories.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, CategoryDTO> categoryMap = layoutCategories.stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getDisplayOrder()))
                .collect(Collectors.toMap(CategoryDTO::getComparisonName, Function.identity()));
        return categoryMap;
    }

    @Override
    public List<SourceDTO> retrieveListSources(Long id) {
        List<SourceDTO> sources = itemRepository.findDishSourcesForList(id);
        sources.addAll(itemRepository.findListSourcesForList(id));
        return sources;
    }

    @Override
    public ShoppingListDTO getListDTOForUser(Long userId, Long listId) {
        return shoppingListRepository.findDTOByListIdAndUserId(listId, userId);
    }

    @Override
    public Map<Long, String> retrieveUnitMapping(Long listId) {
        Set<UnitEntity> units = new HashSet<>(itemRepository.findUnitsForItems(listId));
        units.addAll(itemRepository.findUnitsForItemDetails(listId));

        return units.stream().collect(Collectors.toMap(UnitEntity::getId, UnitEntity::getName));
    }


    public ShoppingListDTO getStarterList(Long userId) {

        List<ShoppingListDTO> foundLists = shoppingListRepository.findDTOByUserIdAndIsStarterListTrue(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    public ShoppingListDTO getMostRecentList(Long userId) {

        List<ShoppingListDTO> foundLists = shoppingListRepository.findByUserId(userId);
        if (!foundLists.isEmpty()) {
            Long listId = foundLists.get(0).getListId();
            return shoppingListRepository.findDTOById(listId);
        }
        return null;
    }

    public ShoppingListEntity updateList(Long userId, Long listId, ShoppingListDTO updateFrom) {
        // get list
        Optional<ShoppingListEntity> byUserNameAndId = shoppingListRepository.findByListIdAndUserId(listId, userId);
        if (byUserNameAndId.isEmpty()) {
            throw new ObjectNotFoundException(String.format("List [%s] not found for user [%s] in updateList", listId, userId));
        }
        ShoppingListEntity copyTo = byUserNameAndId.get();

        // check starter list change
        boolean starterListChanged = updateFrom.isStarterList() && !copyTo.getIsStarterList();

        // copy fields from updateFrom
        copyTo.setIsStarterList(updateFrom.isStarterList());
        copyTo.setName(updateFrom.getName());

        if (starterListChanged) {
            ShoppingListDTO oldStarter = getStarterList(userId);
            if (oldStarter != null && !oldStarter.getListId().equals(copyTo.getId())) {
                setStarterList(userId, oldStarter.getListId(), false);
                copyTo.setIsStarterList(true);
            }
        }

        // save changed list
        copyTo.setLastUpdate(new Date());
        return shoppingListRepository.save(copyTo);
    }

    private void setStarterList(Long userId, Long listId, boolean isStarterList) {
        shoppingListRepository.updateStarterList(userId, listId, isStarterList);
    }

}
