package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetProposalEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TargetProposalServiceImpl implements TargetProposalService {

    @Autowired
    private DishSearchService dishSearchService;

    @Override
    public TargetProposalEntity createTargetProposal(TargetEntity target) {
        if (target == null) {
            return null;
        }
        if (target.getSlots() == null || target.getSlots().isEmpty()) {
            return null;
        }
        // determine maximum dishes returned per slot, slot count
        int slotcount = target.getSlots().size();
        TargetProposalSettings settings = determineSettings(slotcount);


        // get database info for slots
        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, settings.getMaximumEmpties());

        // get list of approaches (order in which slots are assembled)
        //List<ProposalAttempt> approaches = getProposalApproaches(settings.getApproachType(), slotcount, settings.getProposalCount());

        // process each proposal attempt
        List<AttemptResult> results = getAttemptResults(settings, slotcount, rawResults, settings.getDishCountPerSlot());

        // sort for best results
        boolean byMedian = false;
        results = sortForBestResults(results, byMedian);

        // assign best approach to new proposal
        if (results.get(0) != null) {
            TargetProposalEntity proposal = createProposalFromAttempt(results.get(0), target);
        return proposal;
        }
        return null;
    }

    private List<AttemptResult> getAttemptResults(TargetProposalSettings settings, int slotcount, List<RawSlotResult> rawResults, int dishCountPerSlot) {
        List<ProposalAttempt> proposals = getProposalApproaches(settings.getApproachType(), slotcount, settings.getProposalCount());

        //process each proposal, collecting results
        List<AttemptResult> results = new ArrayList<>();
        for (ProposalAttempt proposal : proposals) {
            // run single proposal
            AttemptResult result = processSingleProposal(proposal, rawResults, settings.getDishCountPerSlot());
            results.add(result);
        }


        return results;
    }

    private TargetProposalSettings determineSettings(int slotcount) {
        TargetProposalSettings settings = new TargetProposalSettings();
        settings.setMaximumEmpties(5);
        settings.setDishCountPerSlot(5);
        if (slotcount < 3) {
            settings.setApproachType(ApproachType.WHEEL);
            settings.setProposalCount(slotcount);
            return settings;
        }
        settings.setApproachType(ApproachType.WHEEL_MIXED);
        settings.setProposalCount(Math.min(slotcount + 1, 10));
        return settings;
    }

    private TargetProposalEntity createProposalFromAttempt(AttemptResult proposalAttempt, TargetEntity target) {
        return null;
    }

    private List<AttemptResult> sortForBestResults(List<AttemptResult> results, boolean byMedian) {

        results.sort(Comparator.comparing(AttemptResult::getHealthIndexMedian)
                .thenComparing(AttemptResult::getHealthIndexAverage).reversed());
        return results;
    }

    private List<ProposalAttempt> processProposals(List<ProposalAttempt> proposals, List<RawSlotResult> rawResults, int dishesPerSlot) {


        return null;
    }

    private AttemptResult processSingleProposal(ProposalAttempt proposal, List<RawSlotResult> rawResults, int dishesPerSlot) {
        // clear all filters
        rawResults.stream().forEach(t -> t.clearFilteredDishes());
        // cycle through proposal order
        Integer[] cycle = proposal.getAttemptOrder();
        for (int i = 0; i < cycle.length; i++) {
            RawSlotResult rawResult = rawResults.get(cycle[i]);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(dishesPerSlot);
            proposal.setDishMatches(i, dishMatches);
            for (int j = i + 1; j < cycle.length; j++) {
                rawResults.get(cycle[j]).addDishesToFilter(dishMatches);
            }
        }
        return proposal.finalizeResults();

    }

    private List<ProposalAttempt> getProposalApproaches(ApproachType approachType, int slotcount, int proposalcount) {


        List<Integer[]> approachOrders = AttemptGenerator.getProposalOrders(approachType, slotcount, proposalcount);

        List<ProposalAttempt> proposalAttempts = new ArrayList<>();
        for (Integer[] order : approachOrders) {
            ProposalAttempt proposalAttempt = new ProposalAttempt(order);
            proposalAttempts.add(proposalAttempt);
        }
        return proposalAttempts;
    }


    private ApproachType getApproachType(int slotcount) {
        return null;
    }

    private List<RawSlotResult> retrieveRawResults(TargetEntity target, int maxempties) {
        // get target tags
        List<String> targetTagIds = target.getTagIdsAsList();
        List<TargetSlotEntity> targetSlots = target.getSlots();
        Long userId = target.getUserId();

        // retrieve results for each slot
        List<RawSlotResult> resultList = new ArrayList<>();
        for (TargetSlotEntity slot : targetSlots) {
            List<String> tagListForSlot = new ArrayList<>();
            tagListForSlot.addAll(targetTagIds);
            tagListForSlot.addAll(slot.getTagIdsAsList());

            // query db
            List<DishTagSearchResult> dishResults = dishSearchService.retrieveDishResultsForTags(userId, slot.getSlotDishTagId(), targetTagIds.size(), tagListForSlot);
            List<DishTagSearchResult> matches = new ArrayList<>();
            List<DishTagSearchResult> targetMatches = new ArrayList<>();
            List<DishTagSearchResult> emptyMatches = new ArrayList<>();
            dishResults.stream()
                    .forEach(m -> {
                        if (m.getSlotMatches() > 0) {
                            matches.add(m);
                        } else if (m.getTotalMatches() - m.getSlotMatches() > 0) {
                            targetMatches.add(m);
                        } else {
                            emptyMatches.add(m);
                        }
                    });
            int end = emptyMatches.size();
            if (emptyMatches.size() > maxempties) {
                end = maxempties;

            }
            // a word about sorting - the results are sorted by last_added date from the database.  Additional
            // sorting by match counts (full and slot) is done within RawSlotResults
            RawSlotResult rawSlotResult = new RawSlotResult(slot.getId(), matches, targetMatches, emptyMatches.subList(0, end), tagListForSlot);
            resultList.add(rawSlotResult);
        }

        // sort results by total dishes found
        resultList.sort(Comparator.comparing(RawSlotResult::getRawMatchCount));
        return resultList;
    }
}
