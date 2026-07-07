/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;
import com.meg.listshop.lmt.data.pojos.ListItemDTO;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.ShoppingListDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional(rollbackFor = ItemProcessingException.class)
public abstract class BaseShoppingListService  {
    private static final Logger logger = LoggerFactory.getLogger(BaseShoppingListService.class);

    protected final TagService tagService;
    protected final DishService dishService;
    protected final ShoppingListRepository shoppingListRepository;
    protected final MealPlanService mealPlanService;
    protected final ItemRepository itemRepository;
    protected final ListTagStatisticService listTagStatisticService;
    protected final ListItemStateMachine listItemStateMachine;

    protected final ItemChangeRepository itemChangeRepository;

    @Value("${service.shoppinglistservice.default.list.name}")
    public String defaultShoppingListName;


    public BaseShoppingListService(TagService tagService,
                                     DishService dishService,
                                     ShoppingListRepository shoppingListRepository,
                                     MealPlanService mealPlanService,
                                     ItemRepository itemRepository,
                                     ItemChangeRepository itemChangeRepository,
                                     ListTagStatisticService listTagStatisticService,
                                     ListItemStateMachine listItemStateMachine) {
        this.tagService = tagService;
        this.dishService = dishService;
        this.shoppingListRepository = shoppingListRepository;
        this.mealPlanService = mealPlanService;
        this.itemRepository = itemRepository;
        this.itemChangeRepository = itemChangeRepository;
        this.listTagStatisticService = listTagStatisticService;
        this.listItemStateMachine = listItemStateMachine;
    }




    

    public void performItemOperation(Long userId, Long sourceListId, ItemOperationType operationType, List<Long> tagIds, Long destinationListId) throws ItemProcessingException {
        logger.debug("Beginning performItemOperation with sourceListId [{}], destinationListId[{}],  tagIds [{}] and itemOperationType [{}]", sourceListId, destinationListId, tagIds, operationType);
        // get source list
        ShoppingListEntity sourceList = getListForUserById(userId, sourceListId);

        if (sourceList == null) {
            return;
        }
        switch (operationType) {
            case RemoveCrossedOff, RemoveAll, Copy, Move, Remove:
                doMoveRemoveItemOperations(sourceList, userId, sourceListId, operationType, tagIds, destinationListId);
                break;
            case CrossOff, UnCrossOff:
                doCrossOffActions(sourceList, operationType, tagIds);
                break;
        }
    }

    protected void doCrossOffActions(ShoppingListEntity sourceList, ItemOperationType operationType, List<Long> tagIds) {
        // get item
        List<ListItemEntity> items = sourceList.getItems();

        Date crossOffDate = operationType.equals(ItemOperationType.CrossOff) ? new Date() : null;

        items.stream().filter(i -> i.getRemovedOn() == null)
                .filter(i -> tagIds.contains(i.getTag().getId()))
                .forEach(i -> i.setCrossedOff(crossOffDate));

        sourceList.setLastUpdate(new Date());
        itemRepository.saveAll(items);

    }

    public void doMoveRemoveItemOperations(ShoppingListEntity sourceList, Long userId, Long sourceListId, ItemOperationType operationType,
                                           List<Long> tagIds, Long destinationListId) throws ItemProcessingException {

        List<ListItemEntity> operationItems = null;
        if (operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {
            operationItems = getListItemsForOperationType(operationType, sourceList);
        } else {
            operationItems = sourceList.getItems().stream()
                    .filter(item -> tagIds.contains(item.getTag().getId()))
                    .toList();
        }

        if (operationItems == null || operationItems.isEmpty()) {
            return;
        }

        // if operation requires copy, get destinationList and copy
        if (operationType.equals(ItemOperationType.Copy) ||
                operationType.equals(ItemOperationType.Move)) {
            ShoppingListEntity targetList = getListForUserById(userId, destinationListId);
            Map<Long, ListItemEntity> destinationMap = targetList.getItems().stream()
                    .filter(item -> tagIds.contains(item.getTag().getId()))
                    .collect(Collectors.toMap(item -> item.getTag().getId(), item -> item));
            List<ListItemEntity> addedItems = new ArrayList<>();

            for (ListItemEntity item : operationItems) {
                ListItemEntity existingItem = destinationMap.get(item.getTag().getId());
                ItemStateContext context = new ItemStateContext(existingItem, destinationListId);
                context.setListItem(item);
                ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, userId);

                addedItems.add(result);
            }

            saveListChanges(targetList, addedItems, ListOperationType.LIST_ADD);
        }

        // if operation requires remove, remove from source
        if (operationType.equals(ItemOperationType.Move) ||
                operationType.equals(ItemOperationType.Remove) ||
                operationType.equals(ItemOperationType.RemoveCrossedOff) ||
                operationType.equals(ItemOperationType.RemoveAll)) {

            List<ListItemEntity> changedItems = new ArrayList<>();
            List<ListItemEntity> removedItems = new ArrayList<>();
            for (ListItemEntity item : operationItems) {
                ItemStateContext context = new ItemStateContext(item, sourceListId);
                context.setTag(item.getTag());
                ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context, userId);
                if (result == null) {
                    removedItems.add(item);
                } else {
                    changedItems.add(result);
                }
            }

            saveListChanges(sourceList, changedItems, removedItems, ListOperationType.LIST_REMOVE);
        }

    }

    
    public void addDishesToList(Long userId, Long listId, ListAddProperties listAddProperties) throws ShoppingListException, ItemProcessingException {
        // retrieve list
        ShoppingListEntity list = getListForUserById(userId, listId);
        if (list == null) {
            throw new ObjectNotFoundException(String.format("No list found for user [%s] with list id [%s])", userId, listId));
        }

        // get dishes to add
        List<Long> dishIds = listAddProperties.getDishSourceIds();
        if (dishIds.isEmpty()) {
            return;
        }

        doAddDishesToList(userId, list, dishIds);
    }

    private void doAddDishesToList(Long userId, ShoppingListEntity list, List<Long> dishIds) throws ShoppingListException, ItemProcessingException {
        // get dish items
        List<DishItemEntity> dishItems = dishService.getDishItems(userId, dishIds);

        List<ListItemEntity> changedItems = addDishItemsToList(list, dishItems);

        saveListChanges(list, changedItems, ListOperationType.DISH_ADD);
    }

    
    public ShoppingListEntity generateListForUser(Long userId, ListGenerateProperties listGenerateProperties) throws ShoppingListException, ItemProcessingException {
        // check list name
        String listNameFromProperties = listGenerateProperties.getListName();
        if (listNameFromProperties == null || listNameFromProperties.isEmpty()) {
            listNameFromProperties = defaultShoppingListName;
        }
        String listName = ensureListNameIsUnique(userId, listNameFromProperties);
        // create list
        ShoppingListEntity newList = createList(userId, listName);

        // get dishes to add
        List<Long> dishIds = new ArrayList<>();
        if (listGenerateProperties.getDishSourcesIds() != null) {
            dishIds = listGenerateProperties.getDishSourcesIds();
        } else if (listGenerateProperties.getMealPlanSourceId() != null) {
            // get dishIds for meal plan
            MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, listGenerateProperties.getMealPlanSourceId());
            dishIds = new ArrayList<>();
            if (mealPlan.getSlots() != null) {
                for (SlotEntity slot : mealPlan.getSlots()) {
                    dishIds.add(slot.getDish().getId());
                }
            }
        }

        // now, add all dish ids
        doAddDishesToList(userId, newList, dishIds);

        // add starter list - if desired
        if (Boolean.TRUE.equals(listGenerateProperties.getAddFromStarter())) {
            // add Items from BaseList
            ShoppingListEntity baseList = getStarterList(userId);
            if (baseList != null) {
                doAddListToList(newList, baseList);
            }
        }

        // check about generating a meal plan
        generateMealPlanOnListCreate(userId, listGenerateProperties);

        // save changes
        saveListChanges(newList, newList.getItems(), ListOperationType.NONE);
        return newList;

    }

    private ShoppingListEntity getStarterList(Long userId) {

        List<ShoppingListEntity> foundLists = shoppingListRepository.findByUserIdAndIsStarterListTrue(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    private String ensureListNameIsUnique(Long userId, String listName) {
        // does this name already exist for the user?
        List<ShoppingListEntity> existing = shoppingListRepository.findByUserIdAndName(userId, listName);

        if (existing.isEmpty()) {
            return listName;
        }

        // if so, get all lists with names starting with the listName
        List<ShoppingListEntity> similar = shoppingListRepository.findByUserIdAndNameLike(userId, listName + "%");
        List<String> similarNames = similar.stream()
                .map(list -> list.getName().trim().toLowerCase()).toList();
        // use handy StringTools method to get first unique name

        return StringTools.makeUniqueName(listName, similarNames);
    }

    private void generateMealPlanOnListCreate(Long userId, ListGenerateProperties listGenerateProperties) {
        if (Boolean.TRUE.equals(listGenerateProperties.getGenerateMealplan()) &&
                listGenerateProperties.getMealPlanSourceId() == null &&
                listGenerateProperties.getDishSourcesIds() != null) {
            MealPlanEntity mp = mealPlanService.createMealPlan(userId, new MealPlanEntity());
            for (Long ds : listGenerateProperties.getDishSourcesIds()) {
                mealPlanService.addDishToMealPlan(userId, mp.getId(), ds);
            }
        }
    }




    public ShoppingListEntity getListForUserById(Long userId, Long listId) {
        final String message = String.format("Retrieving List for id %d and user_id %s", listId, userId);
        logger.debug(message);

        Optional<ShoppingListEntity> shoppingListEntityOpt;
        shoppingListEntityOpt = shoppingListRepository.getWithItemsByListId(listId);

        // may be a list which doesn't have items.  Check for that here
        if (shoppingListEntityOpt.isEmpty()) {
            shoppingListEntityOpt = shoppingListRepository.findById(listId);
        }
        ShoppingListEntity shoppingListEntity = shoppingListEntityOpt.orElse(null);
        if (shoppingListEntity != null && shoppingListEntity.getUserId().equals(userId)) {
            return shoppingListEntity;
        }
        return null;
    }

    
    public ShoppingListEntity getSimpleListForUserById(Long userId, Long listId) {
        final String message = String.format("Retrieving List for id %d and user_id %s", listId, userId);
        logger.debug(message);

        Optional<ShoppingListEntity> shoppingListEntityOpt;
        shoppingListEntityOpt = shoppingListRepository.findByListIdAndUserId(listId, userId);

        return shoppingListEntityOpt.orElse(null);
    }


    public List<ShoppingListDTO> getListsByUserId(Long userId) {
        return shoppingListRepository.findByUserId(userId);
    }
    @Transactional
    public void deleteList(Long userId, Long listId) {
        List<ShoppingListDTO> allLists = getListsByUserId(userId);
        if (allLists == null || allLists.isEmpty()) {
            throw new ActionInvalidException(String.format("No lists found for user [%s]", userId));
        }
        if (allLists.size() < 2) {
            throw new ActionInvalidException(String.format("Can't delete the last list for user [%s]", userId));
        }
        Optional<ShoppingListDTO> toDeleteOpt = allLists.stream()
                .filter(l -> l.getListId().equals(listId)).findFirst();
        if (toDeleteOpt.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Can't find list [%s] for userName [%s] to delete.", listId, userId));
        }

        ShoppingListDTO toDelete = toDeleteOpt.get();

        shoppingListRepository.delete(toDelete.getListId());
    }

    
    public void addItemToListByTag(Long userId, Long listId, Long tagId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ListItemEntity item = shoppingListEntity.getItems().stream()
                .filter(l -> l.getTag().getId().equals(tagId))
                .findFirst()
                .orElse(null);
        boolean isNew = item == null;

        TagEntity tag = tagService.getTagById(tagId);
        ItemStateContext itemStateContext = new ItemStateContext(item, listId);
        itemStateContext.setTag(tag);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, userId);

        if (isNew) {
            shoppingListEntity.getItems().add(result);
        }

        saveListChanges(shoppingListEntity,
                Collections.singletonList(result),
                ListOperationType.TAG_ADD);
    }

    
    public void updateItemCount(Long userId, Long listId, Long tagId, Integer usedCount) {
        if (usedCount == null) {
            throw new ActionInvalidException("usedCount is null in updateItemCount.");
        }

        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        ListItemEntity item = itemRepository.getItemByListAndTag(listId, tagId);
        if (item == null) {
            throw new ObjectNotFoundException("no item found in list [" + listId + "] with tagid [" + tagId + "]");
        }

        // set fields in item
        item.setUsedCount(usedCount);
        item.setUpdatedOn(new Date());
        item.setRemovedOn(null);
        item.setCrossedOff(null);

        // update item
        itemRepository.save(item);

        // update list date
        shoppingListEntity.setLastUpdate(new Date());
        shoppingListRepository.save(shoppingListEntity);
    }

    
    public void deleteAllItemsFromList(Long userId, Long listId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getSimpleListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        List<ListItemEntity> itemEntities = itemRepository.findByListId(listId);
        if (itemEntities == null) {
            return;
        }

        List<ListItemEntity> updatedItems = new ArrayList<>();
        List<ListItemEntity> deletedItems = new ArrayList<>();
        for (ListItemEntity item : itemEntities) {
            ItemStateContext context = new ItemStateContext(item, listId);
            context.setTag(item.getTag());
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context, userId);
            if (result == null) {
                deletedItems.add(item);
            } else {
                updatedItems.add(item);
            }
            updatedItems.add(result);
        }
        saveListChanges(shoppingListEntity, updatedItems, deletedItems, ListOperationType.NONE);
    }

    
    public void deleteItemFromList(Long userId, Long listId, Long itemId) throws ItemProcessingException {
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }
        Optional<ListItemEntity> itemEntityOpt = itemRepository.findById(itemId);
        if (itemEntityOpt.isEmpty()) {
            return;
        }
        ListItemEntity item = itemEntityOpt.get();
        ItemStateContext context = new ItemStateContext(item, listId);
        context.setTag(item.getTag());
        listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context, userId);

        saveListChanges(shoppingListEntity, List.of(item), List.of(item), ListOperationType.LIST_REMOVE);
    }

    
    public ShoppingListEntity generateListFromMealPlan(Long userId, Long mealPlanId) throws ShoppingListException, ItemProcessingException {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity savedNewList = createList(userId, defaultShoppingListName);

        // add to the new list
        return addToListFromMealPlan(userId, savedNewList, mealPlan);

    }

    
    public void addToListFromMealPlan(Long userId, Long listId, Long mealPlanId) throws ShoppingListException, ItemProcessingException {
        // get the mealplan
        MealPlanEntity mealPlan = mealPlanService.getMealPlanForUserById(userId, mealPlanId);

        // create new inprocess list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        addToListFromMealPlan(userId, shoppingList, mealPlan);
    }

    private ShoppingListEntity addToListFromMealPlan(Long userId, ShoppingListEntity shoppingList, MealPlanEntity mealPlan) throws ShoppingListException, ItemProcessingException {
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(SlotEntity::getDish)
                .map(DishEntity::getId)
                .toList();

        // get dish items
        List<DishItemEntity> dishItems = dishService.getDishItems(userId, dishIds);

        List<ListItemEntity> changedItems = addDishItemsToList(shoppingList, dishItems);

        saveListChanges(shoppingList, changedItems, ListOperationType.DISH_ADD);
        Optional<ShoppingListEntity> shoppingListEntity = shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(shoppingList.getId());
        return shoppingListEntity.orElse(null);
    }

    



    private ShoppingListCategory createCategoryModelFromMapping(ItemMappingDTO itemMappingDTO) {

        ShoppingListCategory category = new ShoppingListCategory(itemMappingDTO.getCategoryId());
        category.setUserCategoryId(itemMappingDTO.getUserCategoryId());
        category.setName(itemMappingDTO.getCategoryName());
        category.setDisplayOrder(itemMappingDTO.getDisplayOrder());
        category.setUserDisplayOrder(itemMappingDTO.getUserDisplayOrder());

        return category;
    }

    private void updateCategoryModelFromMapping(ShoppingListCategory category, ItemMappingDTO itemMappingDTO) {
        category.setUserCategoryId(itemMappingDTO.getUserCategoryId());
        category.setName(itemMappingDTO.getCategoryName());
        category.setUserDisplayOrder(itemMappingDTO.getUserDisplayOrder());
    }


    public void addListToList(Long userId, Long listId, Long fromListId) throws ItemProcessingException {
        // get the target list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // get the list to add
        ShoppingListEntity toAdd = getListForUserById(userId, fromListId);
        if (toAdd == null) {
            return;
        }

        doAddListToList(list, toAdd);
    }

    private void doAddListToList(ShoppingListEntity targetList, ShoppingListEntity addFromList) throws ItemProcessingException {
        // get list items for list to add
        List<ListItemEntity> itemsToAdd = addFromList.getItems();

        // get hash of tag ids to list items for target list
        Map<Long, ListItemEntity> tagToItem = targetList.getItems().stream()
                .collect(Collectors.toMap(i -> i.getTag().getId(), item -> item));

        // go through all list items to add, adding item for each list
        List<ListItemEntity> newOrUpdatedListItems = new ArrayList<>();
        for (ListItemEntity itemToAdd : itemsToAdd) {
            ListItemEntity item = tagToItem.get(itemToAdd.getTag().getId());
            boolean isNew = item == null;
            ItemStateContext context = new ItemStateContext(item, targetList.getId());
            context.setListItem(itemToAdd);
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, addFromList.getUserId());

            newOrUpdatedListItems.add(result);
            if (isNew) {
                targetList.getItems().add(result);
            }
        }

        // save list
        saveListChanges(targetList, newOrUpdatedListItems, ListOperationType.LIST_ADD);
    }


    public void addDishToList(Long userId, Long listId, Long dishId) throws ShoppingListException, ItemProcessingException {
        // get the list
        ShoppingListEntity list = getListForUserById(userId, listId);

        // gather tags for dish to add
        if (dishId == null) {
            logger.error("No dish found for null dishId");
            throw new ShoppingListException("No dish found for null dishId.");
        }
        List<DishItemEntity> dishItems = tagService.getItemsForDish(userId, dishId);

        List<ListItemEntity> changedItems = addDishItemsToList(list, dishItems);

        saveListChanges(list, changedItems, ListOperationType.DISH_ADD);
    }

    public void fillSources(ShoppingListEntity result) {
        Long selfListId = result.getId();
        // dish sources
        // gather distinct dish sources for list
        List<Long> dishIds = itemRepository.findDishSourcesForListFromItems(result.getId());

        if (dishIds != null && !dishIds.isEmpty()) {
            // retrieve dishes from database
            List<DishEntity> dishSources = dishService.getDishes(dishIds);
            // set in shopping list
            result.setDishSources(dishSources);
        }

        // list sources
        List<Long> listSourceIds = itemRepository.findListSourcesForListForDetails(result.getId()).stream()
                .filter(id -> !id.equals(selfListId))
                .toList();

        // gather distinct list sources for list
        if (listSourceIds != null && !listSourceIds.isEmpty()) {
            List<ShoppingListEntity> sourceLists = shoppingListRepository.findAllById(listSourceIds);

            // set in shopping list
            result.setListSources(sourceLists);
        }
    }

    public void changeListLayout(Long userId, Long listId, Long layoutId) {
        // get shopping list
        ShoppingListEntity shoppingList = getSimpleListForUserById(userId, listId);
        // set new layout id in shopping list
        shoppingList.setListLayoutId(layoutId);
        // save shopping list
        shoppingListRepository.save(shoppingList);

    }

    
    public void removeDishFromList(Long userId, Long listId, Long dishId) throws ItemProcessingException {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);

        List<Long> tagIdsToRemove = itemRepository.findTagIdsInListByDishId(dishId, listId);

        List<ListItemEntity> changedItems = new ArrayList<>();
        List<ListItemEntity> removedTagIds = new ArrayList<>();
        for (ListItemEntity item : shoppingList.getItems()) {
            Long tagId = item.getTag().getId();
            if (tagIdsToRemove.contains(tagId)) {
                removeDishItem(item, listId, dishId, changedItems, removedTagIds, userId);
            }
        }
        saveListChanges(shoppingList, changedItems, removedTagIds, ListOperationType.DISH_REMOVE);
    }

    private void removeDishItem(ListItemEntity item, Long listId, Long dishId, List<ListItemEntity> changedItems,
                                List<ListItemEntity> removedIds, Long userId) throws ItemProcessingException {
        ItemStateContext context = new ItemStateContext(item, listId);
        context.setDishId(dishId);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, context, userId);
        if (result == null) {
            removedIds.add(item);
        } else {
            changedItems.add(result);
        }
    }

    
    public void removeListItemsFromList(Long userId, Long listId, Long fromListId) throws ItemProcessingException {
        // get list
        ShoppingListEntity shoppingList = getListForUserById(userId, listId);
        Map<Long, ListItemEntity> listItemsByTag = shoppingList.getItems().stream()
                .filter(item -> item.getTag().getId() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), item -> item));

        // get list tag ids to remove
        List<Long> tagIdsToRemove = itemRepository.findTagIdsInListByListId(fromListId, listId);

        List<ListItemEntity> changedItems = new ArrayList<>();
        List<ListItemEntity> removedItems = new ArrayList<>();
        for (Long tagId : tagIdsToRemove) {
            ListItemEntity itemInList = listItemsByTag.get(tagId);
            ItemStateContext testContext = new ItemStateContext(itemInList, listId);
            testContext.setListId(fromListId);
            ListItemEntity resultItems = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, userId);
            if (resultItems != null) {
                changedItems.add(itemInList);
            } else {
                removedItems.add(itemInList);
            }
        }

        saveListChanges(shoppingList, changedItems, removedItems, ListOperationType.LIST_REMOVE);
    }

    
    public void updateItemCrossedOff(Long userId, Long listId, Long itemId, Boolean crossedOff) throws ItemProcessingException {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        Optional<ListItemEntity> itemOpt = shoppingListEntity.getItems().stream()
                .filter(li -> li.getId().equals(itemId))
                .findFirst();
        if (itemOpt.isEmpty()) {
            return;
        }
        ListItemEntity item = itemOpt.get();

        // ensure item belongs to list
        if (item.getListId() == null && !item.getListId().equals(shoppingListEntity.getId())) {
            return;
        }

        ItemStateContext context = new ItemStateContext(item, listId);
        context.setCrossedOff(crossedOff);
        ListItemEntity changedItem = listItemStateMachine.handleEvent(ListItemEvent.CROSS_OFF_ITEM, context, userId);


        saveListChanges(shoppingListEntity, List.of(changedItem), ListOperationType.NONE);
    }

    
    public void crossOffAllItems(Long userId, Long listId, boolean crossOff) throws ItemProcessingException {
        // ensure list belongs to user
        ShoppingListEntity shoppingListEntity = getListForUserById(userId, listId);
        if (shoppingListEntity == null) {
            return;
        }

        // get item
        List<ListItemEntity> items = shoppingListEntity.getItems();

        for (ListItemEntity item : items) {
            ItemStateContext context = new ItemStateContext(item, listId);
            context.setCrossedOff(crossOff);
            listItemStateMachine.handleEvent(ListItemEvent.CROSS_OFF_ITEM, context, userId);

        }

        saveListChanges(shoppingListEntity, items, ListOperationType.NONE);
        itemRepository.saveAll(items);
    }

    protected void legacySaveListChanges(ShoppingListEntity shoppingList, ItemCollector collector, CollectorContext context) {
        itemChangeRepository.legacySaveItemChanges(shoppingList, collector, shoppingList.getUserId(), context);

        // make changes in list object
        for (ListItemEntity toRemove : collector.getRemovedItems()) {
            shoppingList.getItems().remove(toRemove);
        }
        for (ListItemEntity changed : collector.getChangedItems()) {
            shoppingList.getItems().remove(changed);
            shoppingList.getItems().add(changed);
        }
        if (collector.hasChanges()) {
            shoppingList.setLastUpdate(new Date());
        }
        shoppingListRepository.save(shoppingList);
    }

    protected void saveListChanges(ShoppingListEntity shoppingList, List<ListItemEntity> items,
                                 ListOperationType operationType) {
        itemChangeRepository.saveItemChangeStatistics(shoppingList, items, Collections.emptyList(), shoppingList.getUserId(), operationType);
        // make changes in list object
        if (items != null && !items.isEmpty()) {
            shoppingList.setLastUpdate(new Date());
            shoppingListRepository.save(shoppingList);
        }
    }

    private void saveListChanges(ShoppingListEntity shoppingList, List<ListItemEntity> changedItems,
                                 List<ListItemEntity> removedItems, ListOperationType operationType) {
        List<Long> removedTagIds = removedItems.stream()
                .map(ListItemEntity::getTag)
                .map(TagEntity::getId)
                .toList();
        itemChangeRepository.saveItemChangeStatistics(shoppingList, changedItems, removedTagIds, shoppingList.getUserId(), operationType);
        removedItems.forEach(removed -> shoppingList.getItems().remove(removed));

        // make changes in list object
        if ((changedItems != null && !changedItems.isEmpty()) ||
                (!removedItems.isEmpty())) {
            shoppingList.setLastUpdate(new Date());
            shoppingListRepository.save(shoppingList);
        }
    }

    protected void checkReplaceTagsInCollector(ItemCollector mergeCollector) {
        Set<Long> allServerTagIds = new HashSet<>(mergeCollector.getAllTagIds());

        if (allServerTagIds.isEmpty()) {
            return;
        }
        List<TagEntity> outdatedTags = tagService.getReplacedTagsFromIds(allServerTagIds);
        if (!outdatedTags.isEmpty()) {
            Set<Long> outdatedIds = outdatedTags.stream().map(TagEntity::getReplacementTagId).collect(Collectors.toSet());
            Map<Long, TagEntity> outdatedDictionary = tagService.getDictionaryForIds(outdatedIds);

            mergeCollector.replaceOutdatedTags(outdatedTags, outdatedDictionary);
        }

    }



    protected void checkTagConflict(Long userId, Set<Long> tagKeys, Map<String, ListItemEntity> mergeMap) {
        List<LongTagIdPairDTO> conflicts = tagService.getStandardUserDuplicates(userId, tagKeys);
        for (LongTagIdPairDTO conflict : conflicts) {
            ListItemEntity replaceItem = mergeMap.get(String.valueOf(conflict.getLeftId()));
            if (replaceItem != null) {

                replaceItem.setTagId(conflict.getRightId());
                if (replaceItem.getTag() != null) {
                    replaceItem.getTag().setId(conflict.getRightId());
                }
                mergeMap.put(String.valueOf(conflict.getRightId()), replaceItem);
                mergeMap.remove(String.valueOf(conflict.getLeftId()));
            }
        }

    }

    protected void addItemToClientMap(ListItemEntity item, Map<Long, ListItemEntity> itemMap) {
        if (item.getTag() == null) {
            return;
        }
        Long tagId = item.getTag().getId();
        ListItemEntity toAddTo = itemMap.get(tagId);
        if (itemMap.containsKey(tagId)) {
            int count = toAddTo.getUsedCount() != null ? toAddTo.getUsedCount() : 0;
            toAddTo.setUsedCount(count + 1);
            toAddTo.setRemovedOn(DateUtils.maxDate(toAddTo.getRemovedOn(), item.getRemovedOn()));
            toAddTo.setCrossedOff(DateUtils.maxDate(toAddTo.getCrossedOff(), item.getCrossedOff()));
            toAddTo.setUpdatedOn(DateUtils.maxDate(toAddTo.getUpdatedOn(), item.getUpdatedOn()));
            toAddTo.setAddedOn(DateUtils.maxDate(toAddTo.getAddedOn(), item.getAddedOn()));
            itemMap.put(tagId, toAddTo);
            return;
        }
        itemMap.put(tagId, item);
    }


    private List<ListItemEntity> addDishItemsToList(ShoppingListEntity shoppingList, List<DishItemEntity> dishItems) throws ShoppingListException, ItemProcessingException {
        List<TagType> tagTypesToExclude = Arrays.asList(TagType.DishType, TagType.Rating);
        List<ListItemEntity> items = shoppingList.getItems();
        List<DishItemEntity> dishItemsToAdd = dishItems.stream()
                .filter( i -> !tagTypesToExclude.contains( i.getTag().getTagType()))
                .toList();
        // gather tags for dish to add
        if (dishItemsToAdd == null || dishItems.isEmpty()) {
            return new ArrayList<>();
        }

        // tag ids for dish items
        Set<Long> tagIdsInDish = dishItemsToAdd.stream()
                .map(DishItemEntity::getTag)
                .map(TagEntity::getId)
                .collect(Collectors.toSet());

        // create hash of tag_id to list_items
        Map<Long, ListItemEntity> tagToItem = items.stream()
                .filter(item -> tagIdsInDish.contains(item.getTag().getId()))
                .collect(Collectors.toMap(i -> i.getTag().getId(), item -> item));

        List<ListItemEntity> newOrUpdatedListItems = new ArrayList<>();
        List<Long> addedDishIds = new ArrayList<>();
        for (DishItemEntity dishItemToAdd : dishItemsToAdd) {

            ListItemEntity item = tagToItem.get(dishItemToAdd.getTag().getId());
            boolean isNew = item == null;
            ItemStateContext context = new ItemStateContext(item, shoppingList.getId());
            context.setDishItem(dishItemToAdd);
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, shoppingList.getUserId());

            addedDishIds.add(dishItemToAdd.getDish().getId());
            newOrUpdatedListItems.add(result);
            if (isNew) {
                shoppingList.getItems().add(result);
                tagToItem.put(dishItemToAdd.getTag().getId(), result);
            }

        }
        // update last added date for dish
        this.dishService.updateLastAddedForDishes(addedDishIds);

        return newOrUpdatedListItems;
    }

    private ShoppingListEntity createList(Long userId, String listName) {
        ShoppingListEntity newList = new ShoppingListEntity();

        Long listLayoutId = getDefaultListLayoutId(userId);
        newList.setListLayoutId(listLayoutId);

        newList.setName(listName);
        newList.setIsStarterList(false);
        newList.setCreatedOn(new Date());
        newList.setUserId(userId);
        return shoppingListRepository.save(newList);
    }

    protected abstract Long getDefaultListLayoutId(Long userId);

    private List<ListItemEntity> getListItemsForOperationType(ItemOperationType operationType, ShoppingListEntity sourceList) {
        if (operationType.equals(ItemOperationType.RemoveCrossedOff)) {
            return sourceList.getItems().stream()
                    .filter(item -> item.getCrossedOff() != null)
                    .toList();
        } else if (operationType.equals(ItemOperationType.RemoveAll)) {
            return sourceList.getItems();
        }
        return new ArrayList<>();
    }

}
