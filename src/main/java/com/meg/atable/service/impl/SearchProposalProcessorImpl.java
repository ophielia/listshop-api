package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.data.entity.*;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
@Qualifier("newSearch")
public class SearchProposalProcessorImpl extends AbstractProposalProcessor {


    @Override
    public ProcessResult processProposal(ProposalRequest request) {
        // initialize request object
        ProcessInformation info = fillProcessInfo(request);

        // retrieve raw results
        List<NewRawSlotResult> rawSearchSlotResults = getRawSlotResults(info.getSearchSlots(), request.getTarget(), info);

        // do search for approach with search slots
        if (rawSearchSlotResults == null || rawSearchSlotResults.isEmpty()) {
            // MM throw exception here
            return null;
        }
        Map<Integer, Integer> indexToSlotNumber = new HashMap<>();
        Map<Integer, NewRawSlotResult> resultsBySlot = new HashMap<>();
        for (int i = 0; i < rawSearchSlotResults.size(); i++) {
            NewRawSlotResult slotResult = rawSearchSlotResults.get(i);
            indexToSlotNumber.put(i, slotResult.getSlotNumber());
            resultsBySlot.put(slotResult.getSlotNumber(), slotResult);
        }
        List<ContextApproachEntity> contextApproaches = processApproaches(rawSearchSlotResults, info, indexToSlotNumber, resultsBySlot);

        // fill best approach
        if (contextApproaches == null || contextApproaches.isEmpty()) {
            // MM throw exception here
            return null;
        }

        // fill non-search slots
        ContextApproachEntity fillApproach = contextApproaches.get(0);
        fillFromApproach(fillApproach, resultsBySlot);

        List<DishTagSearchResult> toFilter = getDishIdsToFilter(resultsBySlot);
        List<NewRawSlotResult> rawFillSlotResults = getRawSlotResults(info.getFillSlots(), request.getTarget(), info);
        for (int i = 0; i < rawFillSlotResults.size(); i++) {
            NewRawSlotResult fill = rawFillSlotResults.get(i);
            fill.addDishesToFilter(toFilter);
            for (int j = i + 1; i < rawFillSlotResults.size(); j++) {
                rawFillSlotResults.get(j).addDishesToFilter(fill.getFilteredMatches(info.getDishCountPerSlot()));
            }
        }

        // fill and return result
        ProcessResult processResult = new ProcessResult(contextApproaches);
        processResult.setCurrentApproachType(info.getApproachType());
        processResult.setCurrentApproach(0);
        List<NewRawSlotResult> allResults = new ArrayList<>();
        if (rawSearchSlotResults != null ) {
            allResults.addAll(rawSearchSlotResults);
        }
        if (rawFillSlotResults != null ) {
            allResults.addAll(rawFillSlotResults);
        }
        List<ProposalSlotEntity> proposalSlots = mapRawSlotsToEntities(info, allResults);

        // MM NewRawSlotResult => ProposalSlotEntity
        processResult.addResults(proposalSlots);
        return processResult;
    }

    protected ProcessInformation fillProcessInfo(ProposalRequest request) {
        ProcessInformation info = new ProcessInformation();
        List<Long> sqlFilter = new ArrayList<>();

        // check if there are any picked dishes in proposal
        List<Integer> fillInSlotNumbers = new ArrayList<>();
        if (request.getProposal() != null) {
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
                fillSlots.add(slot);
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
        info.setProposal(request.getProposal());

        return info;
    }

 }
