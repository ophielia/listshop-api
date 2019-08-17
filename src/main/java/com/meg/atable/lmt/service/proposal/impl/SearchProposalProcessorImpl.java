package com.meg.atable.lmt.service.proposal.impl;

import com.meg.atable.lmt.api.exception.ProposalProcessingException;
import com.meg.atable.lmt.api.model.ApproachType;
import com.meg.atable.lmt.api.model.TargetType;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.service.*;
import com.meg.atable.lmt.service.impl.AbstractProposalProcessor;
import com.meg.atable.lmt.service.proposal.ProcessInformation;
import com.meg.atable.lmt.service.proposal.ProcessResult;
import com.meg.atable.lmt.service.proposal.ProposalRequest;
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
    public ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException {
        // initialize request object
        ProcessInformation info = fillProcessInfo(request);

        // check that we have slots to search for
        if (info.getSearchSlots().isEmpty()) {
            throw new ProposalProcessingException("");
        }
        // retrieve raw results
        List<NewRawSlotResult> rawSearchSlotResults = getRawSlotResults(info.getSearchSlots(), request.getTarget(), info);

        // do search for approach with search slots
        if (rawSearchSlotResults == null || rawSearchSlotResults.isEmpty()) {
            throw new ProposalProcessingException("No search results found whatsoever for ProposalRequest [" + request + "]");
        }

        Map<Integer, Integer> indexToSlotNumber = new HashMap<>();
        Map<Integer, NewRawSlotResult> resultsBySlot = new HashMap<>();
        for (int i = 0; i < rawSearchSlotResults.size(); i++) {
            NewRawSlotResult slotResult = rawSearchSlotResults.get(i);
            indexToSlotNumber.put(i, slotResult.getSlotNumber());
            resultsBySlot.put(slotResult.getSlotNumber(), slotResult);
        }
        List<ContextApproachEntity> contextApproaches = processApproaches(info, indexToSlotNumber, resultsBySlot);

        // fill best approach
        if (contextApproaches == null || contextApproaches.isEmpty()) {
            // TODO throw exception here
            return null;
        }

        // fill non-search slots
        ContextApproachEntity fillApproach = contextApproaches.get(0);
        fillFromApproach(fillApproach, resultsBySlot);

            List<NewRawSlotResult> rawFillSlotResults = new ArrayList<>();
        if (!info.getFillSlots().isEmpty()) {
            List<Long> toFilter = pullDishIdsToFilter(rawSearchSlotResults);
            rawFillSlotResults = getRawSlotResults(info.getFillSlots(), request.getTarget(), info);
            for (int i = 0; i < rawFillSlotResults.size(); i++) {
                NewRawSlotResult fill = rawFillSlotResults.get(i);
                fill.addDishIdsToFilter(toFilter);
                for (int j = i + 1; j < rawFillSlotResults.size(); j++) {
                    rawFillSlotResults.get(j).addDishesToFilter(fill.getFilteredMatches());
                }
            }
        }

        // fill and return result
        ProcessResult processResult = new ProcessResult(contextApproaches);
        processResult.setCurrentApproachType(info.getApproachType());
        processResult.setCurrentApproach(0);
        List<NewRawSlotResult> allResults = new ArrayList<>();
        if (!rawSearchSlotResults.isEmpty() ) {
            allResults.addAll(rawSearchSlotResults);
        }
        if (!rawFillSlotResults.isEmpty()) {
            allResults.addAll(rawFillSlotResults);
        }
        List<ProposalSlotEntity> proposalSlots = mapRawSlotsToEntities(info, allResults);

        processResult.addResults(proposalSlots);
        return processResult;
    }

    protected ProcessInformation fillProcessInfo(ProposalRequest request) {
        ProcessInformation info = new ProcessInformation();
        List<Long> sqlFilter = new ArrayList<>();
        Map<Integer,Integer> dishCountPerSlot = new HashMap<>();

        // check if there are any picked dishes in proposal
        List<Integer> fillInSlotNumbers = new ArrayList<>();
        if (request.getProposal() != null) {
            for (ProposalSlotEntity slot : request.getProposal().getSlots()) {
                if (slot.getPickedDishId() != null) {
                    sqlFilter.add(slot.getPickedDishId());
                    fillInSlotNumbers.add(slot.getSlotNumber());
                    dishCountPerSlot.put(slot.getSlotNumber(),SEARCH_DISH_RESULT_COUNT - 1);
                } else {
                    dishCountPerSlot.put(slot.getSlotNumber(),SEARCH_DISH_RESULT_COUNT );
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
            targetSlotCount++;
                searchSlots.add(slot);
            }
        }
        info.setFillSlots(fillSlots);
        info.setSearchSlots(searchSlots);
        info.setDishCountPerSlotNumber(dishCountPerSlot);
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

        info.setMaximumEmpties(5); // config
        info.setApproachType(approachType);
        info.setProposalCount(proposalCount);
        info.setSearchSlots(searchSlots);
        info.setFillSlots(fillSlots);
        info.setSqlFilter(sqlFilter);
        int resultCount = TargetType.Standard.equals(request.getTarget().getTargetType())?SEARCH_DISH_RESULT_COUNT:SEARCH_DISH_RESULT_COUNT_PICKUP;
        int slotDishCount = targetSlotCount * resultCount;
        info.setResultsPerSlot(slotDishCount);
        info.setProposal(request.getProposal());
        info.setDefaultDishCountPerSlot(resultCount);

        return info;
    }

 }
