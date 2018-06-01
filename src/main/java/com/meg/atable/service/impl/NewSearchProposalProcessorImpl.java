package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.data.entity.*;
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

    private List<ProposalSlotEntity> mapRawSlotsToEntities(ProcessInformation info, List<NewRawSlotResult> rawSlotResults) {
        List<ProposalSlotEntity> resultList = new ArrayList<>();
        if (rawSlotResults != null) {
            for (NewRawSlotResult slot : rawSlotResults) {
                ProposalSlotEntity proposalSlot = new ProposalSlotEntity();
                proposalSlot.setProposal(info.getProposal());
                proposalSlot.setSlotNumber(slot.getSlotNumber());
                proposalSlot.setSlotDishTagId(info.getDishTagBySlotNumber(slot.getSlotNumber()));
                String flatMatchedIds = matchedIdsAsString(slot,info.getTagKeyBySlotNumber(slot.getSlotNumber()));
                proposalSlot.setFlatMatchedTagIds(flatMatchedIds);
                List<DishSlotEntity> dishSlots = mapDishSlots(info,slot, proposalSlot);
                proposalSlot.setDishSlots(dishSlots);
                resultList.add(proposalSlot);
            }
        }

        return resultList;
    }

    private String matchedIdsAsString(NewRawSlotResult slot, List<String> tagKeyBySlotNumber) {
        List<String> matchedIds = new ArrayList<>();

        return null;
    }

    private List<DishSlotEntity> mapDishSlots(ProcessInformation info, NewRawSlotResult slot, ProposalSlotEntity proposalSlot) {
        List<DishSlotEntity> dishSlotResults = new ArrayList<>();
        List<String> tagListForSlot = info.getTagKeyBySlotNumber(slot.getSlotNumber());

        for (DishTagSearchResult singleDish : slot.getFilteredMatches(info.getDishCountBySlotNumber(slot.getSlotNumber()))) {
            DishSlotEntity dish = new DishSlotEntity();
            dish.setDishId(singleDish.getDishId());
            List<String> matchedIds = singleDish.getMatchedTagIds(tagListForSlot);

            dish.setMatchedTagIds(String.join(";", matchedIds));
            dish.setProposalSlot(proposalSlot);
            dishSlotResults.add(dish);
        }

        return dishSlotResults;
    }

    private List<DishTagSearchResult> getDishIdsToFilter(Map<Integer, NewRawSlotResult> resultsBySlot) {
        // MM implement this
        return null;
    }

    private void fillFromApproach(ContextApproachEntity fillApproach, Map<Integer, NewRawSlotResult> resultsBySlot) {
        // MM implement this
    }

    private List<ContextApproachEntity> processApproaches(List<NewRawSlotResult> rawSearchTSlotResults,
                                                          ProcessInformation info, Map<Integer, Integer> indexToSlotNumber,
                                                          Map<Integer, NewRawSlotResult> resultsBySlot) {


        // generate approach orders
        ApproachType approachType = info.getApproachType();

        List<Integer[]> approachOrders = AttemptGenerator.getProposalOrders(approachType,
                resultsBySlot.entrySet().size(), info.getProposalCount(), indexToSlotNumber);

        // create proposal attempts from approach orders
        List<ProposalAttempt> proposalAttempts = new ArrayList<>();
        for (Integer[] order : approachOrders) {
            ProposalAttempt proposalAttempt = new ProposalAttempt(order);
            proposalAttempts.add(proposalAttempt);
        }
        // process each proposal attempt
        List<ProposalAttempt> attempts = processProposals(proposalAttempts, resultsBySlot, info);
        // sort proposal attempts
        attempts.sort(Comparator.comparing(ProposalAttempt::getHealthIndexMedian)
                .thenComparing(ProposalAttempt::getHealthIndexAverage).reversed());

        // create ContextApproachEntities from ProposalAttmpts
        List<ContextApproachEntity> approachEntities = new ArrayList<>();
        Integer approachNumber = 0;
        // return ContextApproachEntities
        for (ProposalAttempt attempt : attempts) {
            ContextApproachEntity approach = new ContextApproachEntity();
            approach.setApproachNumber(approachNumber);
            approach.setInstructions(attempt.getAttemptOrderAsString(";"));
            approachEntities.add(approach);
            approachNumber++;
        }
        return approachEntities;
    }

    private List<ProposalAttempt> processProposals(List<ProposalAttempt> proposals, Map<Integer, NewRawSlotResult> resultsBySlot, ProcessInformation context) {
        Set<Integer> contentChecks = new HashSet<>();

        List<ProposalAttempt> results = new ArrayList<>();
        for (ProposalAttempt proposal : proposals) {
            // run single proposal
            processSingleProposal(proposal, resultsBySlot, context);
            if (!contentChecks.contains(proposal.getProposalContentHash())) {
                results.add(proposal);
                contentChecks.add(proposal.getProposalContentHash());
            }
        }

        return results;
    }

    private void processSingleProposal(ProposalAttempt proposal, Map<Integer, NewRawSlotResult> resultsBySlot, ProcessInformation information) {
        // clear all filters
        for (Map.Entry<Integer, NewRawSlotResult> entry : resultsBySlot.entrySet()) {
            entry.getValue().clearFilteredDishes();
        }
        // cycle through proposal order
        Integer[] cycle = proposal.getSlotNumberOrder();
        for (int i = 0; i < cycle.length; i++) {
            NewRawSlotResult rawResult = resultsBySlot.get(cycle[i]);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(information.getDishCountPerSlot());
            proposal.setDishMatches(i, rawResult.getSlotNumber(), dishMatches);
            for (int j = i + 1; j < cycle.length; j++) {
                NewRawSlotResult otherResult = resultsBySlot.get(cycle[i]);
                otherResult.addDishesToFilter(dishMatches);
            }
        }
        proposal.finalizeResults();
    }


    private List<NewRawSlotResult> getRawSlotResults(List<TargetSlotEntity> slots, TargetEntity target, ProcessInformation info) {
        // get target tags
        Set<String> targetTagIds = target.getTagIdsAsSet();
        Long userId = target.getUserId();

        // get search groups
        Set<Long> allTags = target.getAllTagIds();
        Map<Long, List<Long>> searchGroups = tagStructureService.getSearchGroupsForTagIds(allTags);
        // retrieve results for each slot
        List<NewRawSlotResult> resultList = new ArrayList<>();
        for (TargetSlotEntity slot : slots) {
            NewRawSlotResult rawSlotResult = retrieveSingleSlotResult(slot, userId, targetTagIds, searchGroups, info);
            rawSlotResult.setSlotNumber(slot.getSlotOrder());
            resultList.add(rawSlotResult);
        }

        // sort results by total dishes found
        resultList.sort(Comparator.comparing(NewRawSlotResult::getRawMatchCount));
        return resultList;
    }

    private NewRawSlotResult retrieveSingleSlotResult(TargetSlotEntity slot, Long userId, Set<String> targetTagIds, Map<Long, List<Long>> searchGroups, ProcessInformation information) {
        //MM TODO - sql filter - add to sql!
        List<String> tagListForSlot = new ArrayList<>();
        tagListForSlot.addAll(targetTagIds);
        tagListForSlot.addAll(slot.getTagIdsAsList());
        information.addTagKeyBySlot(slot.getSlotOrder(), tagListForSlot);
        information.setDishTagBySlotNumber(slot.getSlotOrder(), slot.getSlotDishTagId());

        // query db
        List<DishTagSearchResult> dishResults = dishSearchService.retrieveDishResultsForTags(userId, slot.getSlotDishTagId(), targetTagIds.size(), tagListForSlot, searchGroups, information.getSqlFilter());

        List<DishTagSearchResult> slotMatches = new ArrayList<>();
        List<DishTagSearchResult> targetMatches = new ArrayList<>();
        List<DishTagSearchResult> empties = new ArrayList<>();
        int matchCount = 0;
        dishResults
                .forEach(m -> {
                    if (m.getSlotMatches() > 0) {
                        slotMatches.add(m);
                    } else if (m.getTotalMatches() > 0) {
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
            slotMatches.addAll(empties.subList(0, emptyIndex));
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
