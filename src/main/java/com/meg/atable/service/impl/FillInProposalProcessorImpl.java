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
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 24/05/2018.
 */
@Component
@Qualifier("fillInSearch")
public class FillInProposalProcessorImpl extends AbstractProposalProcessor {

    @Override
    public ProcessResult processProposal(ProposalRequest request) throws ProposalProcessingException {
        // initialize request object
        ProcessInformation info = fillProcessInfo(request);

        // check that we have slots to search for
        if (info.getFillSlots().isEmpty()) {
            throw new ProposalProcessingException("No Fill Slots in FillIn Proposal");
        }

        // fill non-search slots
        List<Long> toFilter = info.getCodeFilter();
        List<NewRawSlotResult> rawFillSlotResults = getRawSlotResults(info.getFillSlots(), request.getTarget(), info);
        NewRawSlotResult fillSlot = rawFillSlotResults.get(0);
        // add prefilled ids
        fillSlot.addDishIdsToFilter(toFilter);
        // add code filter
        fillSlot.addPrefilledDishIds(info.getCurrentDishIdsForSlot());


        // fill and return result
        ProcessResult processResult = new ProcessResult(null);
        processResult.setCurrentApproach(info.getCurrentApproachIndex());
        List<NewRawSlotResult> allResults = new ArrayList<>();
        if (!rawFillSlotResults.isEmpty()) {
            allResults.addAll(rawFillSlotResults);
        }
        List<ProposalSlotEntity> proposalSlots = mapRawSlotsToEntities(info, allResults);

        processResult.addResults(proposalSlots);
        return processResult;
    }


    private ProcessInformation fillProcessInfo(ProposalRequest request) {
        ProcessInformation info = new ProcessInformation();
        List<Long> sqlFilter = new ArrayList<>();
        List<Long> codeFilter = new ArrayList<>();
        List<Long> codePrefill = new ArrayList<>();

        Map<Integer, Integer> dishCountPerSlot = new HashMap<>();

        // fill in slot id
        Integer fillInSlotId = request.getFillInSlotNumber();

        // check if there are any picked dishes in proposal
        if (request.getProposal() != null) {
            for (ProposalSlotEntity slot : request.getProposal().getSlots()) {
                if (slot.getPickedDishId() != null) {
                    sqlFilter.add(slot.getPickedDishId());
                }
                if (slot.getSlotNumber().equals(fillInSlotId)) {
                    // this is the one we're filling. add dishes to codePrefill
                    codePrefill.addAll(slot.getDishSlots().stream().map(DishSlotEntity::getDishId).collect(Collectors.toList()));
                    // determine  dish count
                    int fillInCount = slot.getDishSlots().size() + FILL_IN_INCREMENT;
                    dishCountPerSlot.put(slot.getSlotNumber(), fillInCount);
                } else {
                    // add dishes for this slot to the codeFilter
                    codeFilter.addAll(slot.getDishSlots().stream().map(DishSlotEntity::getDishId).collect(Collectors.toList()));
                }
            }
        }


        List<TargetSlotEntity> fillSlots = new ArrayList<>();


        for (TargetSlotEntity slot : request.getTarget().getSlots()) {
            if (slot.getSlotOrder().equals(fillInSlotId)) {
                fillSlots.add(slot);
            }
        }
        info.setFillSlots(fillSlots);
        info.setDishCountPerSlotNumber(dishCountPerSlot);
        info.setCodeFilter(codeFilter);
        info.setCurrentSlotResults(codePrefill);

        // if meal plan part of search, add meal plan ids to sql filter
        if (request.getMealPlan() != null) {
            for (SlotEntity slot : request.getMealPlan().getSlots()) {
                sqlFilter.add(slot.getDish().getId());
            }
        }

        // prepare configuration for fill in
        ProposalContextEntity context = request.getContext();
        info.setCurrentApproachIndex(context.getCurrentApproachIndex());

        info.setMaximumEmpties(5); // config
        info.setFillSlots(fillSlots);
        info.setSqlFilter(sqlFilter);

        int resultCount = TargetType.Standard.equals(request.getTarget().getTargetType())?SEARCH_DISH_RESULT_COUNT:SEARCH_DISH_RESULT_COUNT_PICKUP;
        int slotDishCount = request.getProposal().getSlots().size() * resultCount;
        info.setResultsPerSlot(slotDishCount);
        info.setProposal(request.getProposal());
        info.setDefaultDishCountPerSlot(resultCount);

        return info;
    }

}
