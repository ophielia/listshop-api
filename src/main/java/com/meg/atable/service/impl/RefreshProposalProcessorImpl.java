package com.meg.atable.service.impl;

import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.api.model.TargetType;
import com.meg.atable.data.entity.*;
import com.meg.atable.service.NewRawSlotResult;
import com.meg.atable.service.ProcessInformation;
import com.meg.atable.service.ProcessResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
@Qualifier("refreshSearch")
public class RefreshProposalProcessorImpl extends AbstractProposalProcessor {


    @Override
    public ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException {
        // initialize request object
        ProcessInformation info = fillProcessInfo(request);

        // check that we have slots to search for
        if (info.getSearchSlots().isEmpty()) {
            throw new ProposalProcessingException("No Search Slots in Refresh Proposal");
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

        // fill with refresh approach
        ContextApproachEntity fillApproach = info.getCurrentApproach();
        fillFromApproach(fillApproach, resultsBySlot);

        // fill non-search slots
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
        ProcessResult processResult = new ProcessResult(null);
        processResult.setCurrentApproach(info.getCurrentApproachIndex());
        List<NewRawSlotResult> allResults = new ArrayList<>();
        if (!rawSearchSlotResults.isEmpty()) {
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
        Map<Integer, Integer> dishCountPerSlot = new HashMap<>();
        int resultCount = TargetType.Standard.equals(request.getTarget().getTargetType())?SEARCH_DISH_RESULT_COUNT:SEARCH_DISH_RESULT_COUNT_PICKUP;

        // check if there are any picked dishes in proposal
        List<Integer> fillInSlotNumbers = new ArrayList<>();
        if (request.getProposal() != null) {
            for (ProposalSlotEntity slot : request.getProposal().getSlots()) {
                if (slot.getPickedDishId() != null) {
                    sqlFilter.add(slot.getPickedDishId());
                    fillInSlotNumbers.add(slot.getSlotNumber());
                    dishCountPerSlot.put(slot.getSlotNumber(), resultCount - 1);
                } else {
                    dishCountPerSlot.put(slot.getSlotNumber(), resultCount);
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

        // prepare configuration for refresh
        ProposalContextEntity context = request.getContext();
        int approachIndex = context.getCurrentApproachIndex() + 1;
        if (approachIndex >= request.getContext().getApproaches().size()) {
            approachIndex = approachIndex - request.getContext().getApproaches().size();
        }
        info.setCurrentApproach(context.getApproaches().get(approachIndex));
        info.setCurrentApproachIndex(approachIndex);

        info.setMaximumEmpties(5); // config
        info.setSearchSlots(searchSlots);
        info.setFillSlots(fillSlots);
        info.setSqlFilter(sqlFilter);
        int slotDishCount = targetSlotCount * resultCount;
        info.setResultsPerSlot(slotDishCount);
        info.setProposal(request.getProposal());
        info.setDefaultDishCountPerSlot(resultCount);

        return info;
    }

}
