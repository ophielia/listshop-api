/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.v2.impl;

import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.api.model.v2.MergeRequest;
import com.meg.listshop.lmt.api.model.v2.MergeResult;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.*;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.BaseShoppingListService;
import com.meg.listshop.lmt.list.ListTagStatisticService;

import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.list.v2.ShoppingListService;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Override
    public List<CategoryDTO> retrieveListCategories(Long id) {
        return List.of();
    }

    @Override
    public List<SourceDTO> retrieveListSources(Long id) {
        return List.of();
    }

    @Override
    public ShoppingListDTO getListDTOForUser(Long userId, Long listId) {
        return null;
    }

    @Override
    public Map<Long, String> retrieveUnitMapping(Long listId) {
        return Map.of();
    }


    public ShoppingListDTO getStarterList(Long userId) {

        List<ShoppingListDTO> foundLists = shoppingListRepository.findDTOByUserIdAndIsStarterListTrue(userId);
        if (!foundLists.isEmpty()) {
            return foundLists.get(0);
        }
        return null;
    }

    public com.meg.listshop.lmt.api.model.v2.MergeResult mergeFromClient(Long userId, com.meg.listshop.lmt.api.model.MergeRequest mergeRequest) {
        Long listToMergeId = mergeRequest.getListId();
        if (listToMergeId == null) {
            // oops - no list id
            throw new ObjectNotFoundException(String.format("List to merge has empty listId for user [%s]", userId));
        }
        // get list to merge
        ShoppingListEntity list = getListForUserById(userId, listToMergeId);

        if (list == null) {
            // oops - list isn't here any more to merge
            throw new ObjectNotFoundException(String.format("List to merge [%s] not found for user [%s]", listToMergeId, userId));
        }

        if (!requiresMerge(mergeRequest)) {
            // this list doesn't need to be merged
            logger.info("Skipping merge for list [{}].", listToMergeId);
            return new MergeResult();
        }

        logger.info("Offline changes found, proceeding with merge for list [{}].", listToMergeId);
        // create MergeCollector from list
        MergeItemCollector mergeCollector = new MergeItemCollector(list.getId(), list.getItems(), list.getLastUpdate());
        checkReplaceTagsInCollector(mergeCollector);

        // prepare items from client
        List<ListItemEntity> mergeItems = convertClientItemsToItemEntities(userId, mergeRequest);

        // merge from client
        logger.debug("Preparing to merge list [{}].", list.getId());
        mergeCollector.addMergeItems(mergeItems);

        // update after merge
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Merge)
                .withStatisticCountType(StatisticCountType.Single)
                .build();
        legacySaveListChanges(list, mergeCollector, context);

        logger.info("Merge complete for list [{}}].", list.getId());
        return new MergeResult();
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
    protected boolean requiresMerge(com.meg.listshop.lmt.api.model.MergeRequest mergeRequest) {
        // for older clients which aren't sending info - we keep the old behavior, which is to always merge
        if (mergeRequest.getLastOfflineChange() == null && mergeRequest.getLastSynced() == null) {
            return true;
        }
        if (mergeRequest.getLastOfflineChange() == null) {
            return false;
        }
        // last offline change more recent than last synced - we need to merge the offline changes
        return (mergeRequest.getLastSynced() != null &&
                mergeRequest.getLastOfflineChange().after(mergeRequest.getLastSynced()));
    }


}
