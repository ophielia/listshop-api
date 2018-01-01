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
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TargetProposalServiceImpl implements TargetProposalService {

    @Autowired
    private DishSearchService dishSearchService;

    @Override
    public TargetProposalEntity createTargetProposal(TargetEntity target) {
        // determine maximum dishes returned per slot, slot count
        int slotcount = 2;
        int maxempties = 5;

        // get database info for slots
        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, maxempties);

        // get list of approaches (order in which slots are assembled)
        List<ProposalAttempt> approaches = getProposalApproaches(slotcount);

        // process each proposal attempt
        approaches = processProposal(approaches, rawResults);

        // sort for best results
        boolean byMedian = false;
        approaches = sortForBestResults(approaches, byMedian);

        // assign best approach to new proposal
        if (approaches != null) {
        TargetProposalEntity proposal = createProposalFromAttempt(approaches.get(0));
        return proposal;
        }
        return null;
    }

    private TargetProposalEntity createProposalFromAttempt(ProposalAttempt proposalAttempt) {
        return null;
    }

    private List<ProposalAttempt> sortForBestResults(List<ProposalAttempt> approaches, boolean byMedian) {
        return null;
    }

    private List<ProposalAttempt> processProposal(List<ProposalAttempt> approaches, List<RawSlotResult> rawResults) {
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
            List<DishTagSearchResult> matches = dishResults.stream()
                    .filter(d ->
                        d.getTotalMatches() > 0
                    )
                    .collect(Collectors.toList());
            // get either to the end of the list, or the maximum number of empties, whichever is feasible.
            int end = maxempties + matches.size() > dishResults.size() ? dishResults.size() : maxempties + matches.size();
            List<DishTagSearchResult> emptyMatches = dishResults.subList(matches.size(), end);
// MM start here with RawSlotResult processing
            RawSlotResult rawSlotResult = new RawSlotResult(slot, targetTagIds.size(),matches, emptyMatches, tagListForSlot);
            resultList.add(rawSlotResult);
        }
        // end for each

        // sort results by total dishes found

       resultList.sort(Comparator.comparing(RawSlotResult::getRawMatchCount));
        return resultList;
    }
}
