package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.data.entity.ProposalSlotEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public class NewSearchProposalProcessorImpl implements ProposalProcessor {

    @Autowired
    TagStructureService tagStructureService;

    @Autowired
    DishSearchService dishSearchService;

    @Override
    public ProcessResult processProposal(ProposalRequest request) {
        // initialize request object
        ProcessInformation info = fillProcessInfo(request);

        // retrieve raw results
        List<NewRawSlotResult> rawSearchResults = rawSearchResults(request.getTarget(), info);

        // do search for approach with search slots

        // fill best approach

        // fill non-search slots

        // fill and return result
//(will need to have some sort of ProposalApproach => ContextApproach mapper

        return null;
    }

    private List<NewRawSlotResult> rawSearchResults(TargetEntity target, ProcessInformation info) {
        int maxempties = info.getMaximumEmpties();

        // get target tags
        Set<String> targetTagIds = target.getTagIdsAsSet();
        List<TargetSlotEntity> targetSlots = target.getSlots();
        Long userId = target.getUserId();

        // get search groups
        Set<Long> allTags = target.getAllTagIds();
        Map<Long, List<Long>> searchGroups = tagStructureService.getSearchGroupsForTagIds(allTags);
        // retrieve results for each slot
        List<NewRawSlotResult> resultList = new ArrayList<>();
        for (TargetSlotEntity slot : targetSlots) {
            NewRawSlotResult rawSlotResult = retrieveSingleSlotResult(slot, userId,targetTagIds, searchGroups, info);
            resultList.add(rawSlotResult);
        }

        // sort results by total dishes found
        resultList.sort(Comparator.comparing(NewRawSlotResult::getRawMatchCount));
        return resultList;
    }

    private NewRawSlotResult retrieveSingleSlotResult(TargetSlotEntity slot, Long userId, Set<String> targetTagIds, Map<Long, List<Long>> searchGroups, ProcessInformation information) {
        List<String> tagListForSlot = new ArrayList<>();
        tagListForSlot.addAll(targetTagIds);
        tagListForSlot.addAll(slot.getTagIdsAsList());

        // query db
        List<DishTagSearchResult> dishResults = dishSearchService.retrieveDishResultsForTags(userId, slot.getSlotDishTagId(), targetTagIds.size(), tagListForSlot, searchGroups);

        List<DishTagSearchResult> slotMatches = new ArrayList<>();
        List<DishTagSearchResult> targetMatches = new ArrayList<>();
        List<DishTagSearchResult> empties = new ArrayList<>();
        int matchCount =0;
        dishResults
                .forEach(m -> {
                    if (m.getSlotMatches() > 0) {
                        slotMatches.add(m);
                    } else if (m.getTotalMatches()> 0) {
                        targetMatches.add(m);
                    } else {
                        empties.add(m);
                    }
                });

        matchCount = slotMatches.size();
        slotMatches.addAll(targetMatches);
        // adjust list size
        if (slotMatches.size() < information.getResultsPerSlot()) {
            // fill in with empties
            int emptyIndex = Math.min(empties.size(), information.getResultsPerSlot() - slotMatches.size());
            slotMatches.addAll(empties.subList(0,emptyIndex));
        }

        // now, sort this list by slot match, total matches, and last added
        // note - this could be changed into a "mixed" sort later on, which
        // sorts by slot and target matches using a weighted index.
        slotMatches.sort(Comparator.comparing(DishTagSearchResult::getSlotMatches)
                .thenComparing(DishTagSearchResult::getTotalMatches)
                .thenComparing((a, b) -> b.getLastAdded().compareTo(a.getLastAdded())).reversed());

        // a word about sorting - the results are sorted by last_added date from the database.  Additional
        // sorting by match counts (full and slot) is done within RawSlotResults
        return new NewRawSlotResult(slot.getSlotOrder(), slotMatches, tagListForSlot, matchCount);

    }

    private ProcessInformation fillProcessInfo(ProposalRequest request) {
        ProcessInformation info = new ProcessInformation();
        List<Long> sqlFilter = new ArrayList<>();

        // check if there are any picked dishes in proposal
        List<Integer> fillInSlotNumbers = new ArrayList<>();
        if (request.getProposal()!=null) {
            for (ProposalSlotEntity slot : request.getProposal().getSlots()) {
                if (slot.getPickedDishId() != null) {
                    sqlFilter.add(slot.getPickedDishId());
                    fillInSlotNumbers.add(slot.getSlotNumber());
                }
            }
        }
        List<TargetSlotEntity> fillSlots = new ArrayList<>();
        List<TargetSlotEntity> searchSlots = new ArrayList<>();
        int targetSlotCount = 0;

        for (TargetSlotEntity slot : request.getTarget().getSlots()) {
            if (fillInSlotNumbers.contains(slot.getSlotOrder())) {
                fillSlots.add( slot);
            } else {
                searchSlots.add(slot);
            }
            targetSlotCount++;
        }

        // if meal plan part of search, add meal plan ids to sql filter
        if (request.getMealPlan() != null) {
            for (SlotEntity slot : request.getMealPlan().getSlots()) {
                sqlFilter.add(slot.getDish().getId());
            }
        }

        // prepare configuration for search
        ApproachType approachType = ApproachType.WHEEL;
        int proposalCount = targetSlotCount;
        if (targetSlotCount >= 3) {
            approachType = ApproachType.WHEEL_MIXED;
            proposalCount = Math.min(targetSlotCount + 1, 10);
        }

        info.setMaximumEmpties(5);
        info.setDishCountPerSlot(5);
        info.setApproachType(approachType);
        info.setProposalCount(proposalCount);
        info.setSearchSlots(searchSlots);
        info.setFillSlots(fillSlots);
        info.setSqlFilter(sqlFilter);
        int slotDishCount = targetSlotCount * info.getDishCountPerSlot();
        info.setResultsPerSlot(slotDishCount);



        return info;
    }
}
