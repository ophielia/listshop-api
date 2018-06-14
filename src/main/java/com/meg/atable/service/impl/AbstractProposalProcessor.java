package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.data.entity.*;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * Created by margaretmartin on 24/05/2018.
 */
public abstract class AbstractProposalProcessor implements ProposalProcessor {

    @Autowired
    TagStructureService tagStructureService;

    @Autowired
    DishSearchService dishSearchService;


    @Value("${proposal.processor.dish.result.count}")
    protected static final int SEARCH_DISH_RESULT_COUNT =5;


    protected List<ProposalSlotEntity> mapRawSlotsToEntities(ProcessInformation info, List<NewRawSlotResult> rawSlotResults) {
        List<ProposalSlotEntity> resultList = new ArrayList<>();
        if (rawSlotResults != null) {
            for (NewRawSlotResult slot : rawSlotResults) {
                ProposalSlotEntity proposalSlot = new ProposalSlotEntity();
                proposalSlot.setProposal(info.getProposal());
                proposalSlot.setSlotNumber(slot.getSlotNumber());
                proposalSlot.setSlotDishTagId(info.getDishTagBySlotNumber(slot.getSlotNumber()));
                //MM don't need this, I think String flatMatchedIds = matchedIdsAsString(slot,info.getTagKeyBySlotNumber(slot.getSlotNumber()));
                //MM this either proposalSlot.setFlatMatchedTagIds(flatMatchedIds);
                List<DishSlotEntity> dishSlots = mapDishSlots(info,slot, proposalSlot);
                proposalSlot.setDishSlots(dishSlots);
                resultList.add(proposalSlot);
            }
        }

        return resultList;
    }

    protected List<DishSlotEntity> mapDishSlots(ProcessInformation info, NewRawSlotResult slot, ProposalSlotEntity proposalSlot) {
        List<DishSlotEntity> dishSlotResults = new ArrayList<>();
        List<String> tagListForSlot = info.getTagKeyBySlotNumber(slot.getSlotNumber());

        for (DishTagSearchResult singleDish : slot.getFilteredMatches()) {
            DishSlotEntity dish = new DishSlotEntity();
            dish.setDishId(singleDish.getDishId());
            List<String> matchedIds = singleDish.getMatchedTagIds(tagListForSlot);

            dish.setMatchedTagIds(String.join(";", matchedIds));
            dish.setProposalSlot(proposalSlot);
            dishSlotResults.add(dish);
        }

        return dishSlotResults;
    }


    protected void fillFromApproach(ContextApproachEntity fillApproach, Map<Integer, NewRawSlotResult> resultsBySlot) {
        // MM implement this
    }

    protected List<ContextApproachEntity> processApproaches(ProcessInformation info, Map<Integer, Integer> indexToSlotNumber,
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
        List<ProposalAttempt> attempts = processProposals(proposalAttempts, resultsBySlot, indexToSlotNumber,info);
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

    protected List<ProposalAttempt> processProposals(List<ProposalAttempt> proposals, Map<Integer, NewRawSlotResult> resultsBySlot, Map<Integer, Integer> indexToSlotNumber,ProcessInformation context) {
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

    protected void processSingleProposal(ProposalAttempt proposal, Map<Integer, NewRawSlotResult> resultsBySlot, ProcessInformation information) {
        // clear all filters
        for (Map.Entry<Integer, NewRawSlotResult> entry : resultsBySlot.entrySet()) {
            entry.getValue().clearFilteredDishes();
        }
        // cycle through proposal order
        Integer[] cycle = proposal.getSlotNumberOrder();
        for (int i = 0; i < cycle.length; i++) {
            NewRawSlotResult rawResult = resultsBySlot.get(cycle[i]);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches();
            proposal.setDishMatches(rawResult.getSlotNumber(), dishMatches);
            for (int j = i + 1; j < cycle.length; j++) {
                NewRawSlotResult otherResult = resultsBySlot.get(cycle[j]);
                otherResult.addDishesToFilter(dishMatches);
            }
        }
        proposal.finalizeResults();
    }


    protected List<NewRawSlotResult> getRawSlotResults(List<TargetSlotEntity> slots, TargetEntity target, ProcessInformation info) {
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

    protected NewRawSlotResult retrieveSingleSlotResult(TargetSlotEntity slot, Long userId, Set<String> targetTagIds, Map<Long, List<Long>> searchGroups, ProcessInformation information) {
        List<String> tagListForSlot = new ArrayList<>();
        tagListForSlot.addAll(targetTagIds);
        tagListForSlot.addAll(slot.getTagIdsAsList());
        information.addTagKeyBySlot(slot.getSlotOrder(), tagListForSlot);
        information.setDishTagBySlotNumber(slot.getSlotOrder(), slot.getSlotDishTagId());

        // query db
        List<DishTagSearchResult> dishResults = dishSearchService.retrieveDishResultsForTags(userId,slot , targetTagIds.size(), tagListForSlot, searchGroups, information.getSqlFilter());

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
        return new NewRawSlotResult(slot.getSlotOrder(), slotMatches, matchCount, information.getDishCountPerSlot(slot.getSlotOrder()));

    }

}
