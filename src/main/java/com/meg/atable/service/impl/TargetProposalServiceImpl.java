package com.meg.atable.service.impl;

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
        int maxempties = Math.max(slotcount, 5);
        int dishesPerSlot = 5;  // will be configurable

        // get database info for slots
        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, maxempties);

        // get list of approaches (order in which slots are assembled)
        List<ProposalAttempt> approaches = getProposalApproaches(slotcount);

        // process each proposal attempt
        approaches = processProposal(approaches, rawResults, dishesPerSlot);

        // sort for best results
        boolean byMedian = false;
        approaches = sortForBestResults(approaches, byMedian);

        // assign best approach to new proposal
        if (approaches != null) {
            TargetProposalEntity proposal = createProposalFromAttempt(approaches.get(0), target);
        return proposal;
        }
        return null;
    }

    private TargetProposalEntity createProposalFromAttempt(ProposalAttempt proposalAttempt, TargetEntity target) {
        return null;
    }

    private List<ProposalAttempt> sortForBestResults(List<ProposalAttempt> approaches, boolean byMedian) {
        return null;
    }

    private List<ProposalAttempt> processProposal(List<ProposalAttempt> approaches, List<RawSlotResult> rawResults, int dishesPerSlot) {
        return null;
    }

    private List<ProposalAttempt> getProposalApproaches(int slotcount) {
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
            List<DishTagSearchResult> emptyMatches = new ArrayList<>();
            dishResults.stream()
                    .forEach(m -> {
                        if (m.getTotalMatches() > 0) {
                            matches.add(m);
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
            RawSlotResult rawSlotResult = new RawSlotResult(slot.getId(), targetTagIds.size(), matches, emptyMatches.subList(0, end), tagListForSlot);
            resultList.add(rawSlotResult);
        }

        // sort results by total dishes found
        resultList.sort(Comparator.comparing(RawSlotResult::getRawMatchCount));
        return resultList;
    }
}
