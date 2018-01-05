package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.TargetProposalDishRepository;
import com.meg.atable.data.repository.TargetProposalRepository;
import com.meg.atable.data.repository.TargetProposalSlotRepository;
import com.meg.atable.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TargetProposalServiceImpl implements TargetProposalService {

    @Autowired
    private DishSearchService dishSearchService;

    @Autowired
    private TagService tagService;

    @Autowired
    private DishService dishService;

    @Autowired
    private UserService userService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private TargetProposalRepository targetProposalRepository;

    @Autowired
    private TargetProposalSlotRepository targetProposalSlotRepository;

    @Autowired
    private TargetProposalDishRepository targetProposalDishRepository;

    @Override
    public TargetProposalEntity createTargetProposal(String name, Long targetId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        TargetEntity target = targetService.getTargetById(name, targetId);

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
        List<ProposalAttempt> approaches = getProposalApproaches(settings.getApproachType(), slotcount, settings);

        // process each proposal attempt
        processProposals(approaches, rawResults, settings);
        //List<AttemptResult> results = getAttemptResults(settings, slotcount, rawResults, settings.getDishCountPerSlot());

        // sort for best results
        boolean byMedian = false;
        approaches = sortForBestResults(approaches, byMedian);

        // assign best approach to new proposal
        if (approaches.get(0) != null) {
            TargetProposalEntity proposal = createProposalFromAttempt(target, rawResults, approaches, settings);
            proposal.setUserId(user.getId());
            proposal = targetProposalRepository.save(proposal);
        return proposal;
        }
        return null;
    }

    @Override
    public TargetProposalEntity getTargetProposalById(String name, Long proposalId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        TargetProposalEntity proposal = targetProposalRepository.findOne(proposalId);
        if (proposal.getUserId() == null || proposal.getUserId() != user.getId()) {
            return null;
        }

        return proposal;
    }

    @Override
    public TargetProposalEntity fillInformationForProposal(TargetProposalEntity proposalEntity) {
        if (proposalEntity == null) {
            return null;
        }

        // get list of tag ids
        List<Long> tagIds = proposalEntity.getAllTagIds();
        // retrieve tags for ids
        Map<Long, TagEntity> tagDictionary = tagService.getDictionaryForIdList(tagIds);
        // fill in target (and contained slots)
        proposalEntity.fillInAllTags(tagDictionary);

        // get list of dish ids
        List<Long> dishIds = proposalEntity.getAllDishIds();
        Map<Long, DishEntity> dishDictionary = dishService.getDictionaryForIdList(dishIds);
        proposalEntity.fillInAllDishes(dishDictionary);

        return proposalEntity;

    }

    @Override
    public void refreshTargetProposal(String name, Long proposalId) {
// MM to do
        // not yet implemented - this will decide whether to refresh (process single result)
        // or completely regenerate a proposal
    }

    @Override
    public void selectDishInSlot(Principal principal, Long proposalId, Long slotId, Long dishId) {
        TargetProposalEntity proposalEntity = getTargetProposalById(principal.getName(), proposalId);
        if (proposalEntity == null ||
                proposalEntity.getProposalSlots() == null) {
            return;
        }
        TargetProposalSlotEntity slotEntity = getSlotFromProposal(proposalEntity, slotId);

        if (slotEntity == null) {
            return;
        }

        List<TargetProposalDishEntity> dishes = slotEntity.getDishSlotList();
        if (dishes == null) {
            return;
        }
        long dishidcheck = dishId.longValue();
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getDishId().longValue() == dishidcheck) {
                slotEntity.setSelectedDishIndex(i);
            }
        }

        targetProposalRepository.save(proposalEntity);
    }

    private TargetProposalSlotEntity getSlotFromProposal(TargetProposalEntity proposalEntity, Long slotId) {
        if (proposalEntity == null ||
                proposalEntity.getProposalSlots() == null) {
            return null;
        }
        long slotidcheck = slotId.longValue();
        TargetProposalSlotEntity slotEntity = proposalEntity.getProposalSlots().stream()
                .filter(s -> s.getSlotId().longValue() == slotidcheck)
                .findFirst().get();

        return slotEntity;
    }

    @Override
    public void clearDishFromSlot(Principal principal, Long proposalId, Long slotId, Long dishId) {
        TargetProposalEntity proposalEntity = getTargetProposalById(principal.getName(), proposalId);
        if (proposalEntity == null ||
                proposalEntity.getProposalSlots() == null) {
            return;
        }
        TargetProposalSlotEntity slotEntity = getSlotFromProposal(proposalEntity, slotId);

        if (slotEntity == null) {
            return;
        }
        slotEntity.setSelectedDishIndex(-1);
    }

    @Override
    public void refreshTargetProposalSlot(String name, Long proposalId, Long slotId) {
// MM to do
        // refresh dishes for a single slot
    }


    private TargetProposalEntity createProposalFromAttempt(TargetEntity target, List<RawSlotResult> rawResults, List<ProposalAttempt> results, TargetProposalSettings settings) {
        // make a new TargetProposalEntity
        TargetProposalEntity proposalEntity = new TargetProposalEntity(target);
        // set additional information in proposal - regenerateOnRefresh, currentProposalIndex, and proposal list
        proposalEntity.setRegenerateOnRefresh(false);
        proposalEntity.setCurrentProposalIndex(0);
        // go through results pulling attempt order  - flatten into proposal list
        // MM todo

        // save proposal (in order to assign ids)
        proposalEntity = targetProposalRepository.save(proposalEntity);

        // process ProposalAttempt to fill raw results
        ProposalAttempt result = results.get(0);
        processSingleProposal(result, rawResults, settings);

        // get result and fill slots, slotsortorder
        ArrayList<String> slotSortOrder = new ArrayList<>();

        Integer[] order = result.getAttemptOrder();
        for (int i = 0; i < order.length; i++) {
            // get rawResult for order
            RawSlotResult rawResult = rawResults.get(i);
            slotSortOrder.add(String.valueOf(rawResult.getSlotId()));

            TargetSlotEntity targetSlot = target.getSlots().stream().filter(s -> s.getId().longValue() == rawResult.getSlotId()).findFirst().get();
            TargetProposalSlotEntity proposalSlotEntity = new TargetProposalSlotEntity(proposalEntity, targetSlot);
            proposalSlotEntity = targetProposalSlotRepository.save(proposalSlotEntity);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(settings.getDishCountPerSlot());
            for (DishTagSearchResult singleDish : dishMatches) {
                TargetProposalDishEntity dish = new TargetProposalDishEntity();
                dish.setDishId(singleDish.getDishId());
                List<String> matchedIds = singleDish.getMatchedTagIds(rawResult.getTagListForSlot());
                dish.setMatchedTagIds(String.join(";", matchedIds));
                dish.setTargetProposalSlot(proposalSlotEntity);
                dish = targetProposalDishRepository.save(dish);
                proposalSlotEntity.addDish(dish);
            }
            proposalEntity.addSlot(proposalSlotEntity);
        }
        proposalEntity.setSlotSortOrder(String.join(";", slotSortOrder));

        // save entity
        // MM todo
        return proposalEntity;
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

    private List<ProposalAttempt> sortForBestResults(List<ProposalAttempt> results, boolean byMedian) {

        results.sort(Comparator.comparing(ProposalAttempt::getHealthIndexMedian)
                .thenComparing(ProposalAttempt::getHealthIndexAverage).reversed());
        return results;
    }

    private List<ProposalAttempt> processProposals(List<ProposalAttempt> proposals, List<RawSlotResult> rawResults, TargetProposalSettings settings) {
        for (ProposalAttempt proposal : proposals) {
            // run single proposal
            processSingleProposal(proposal, rawResults, settings);
        }

        return proposals;
    }

    private void processSingleProposal(ProposalAttempt proposal, List<RawSlotResult> rawResults, TargetProposalSettings settings) {
        // clear all filters
        rawResults.stream().forEach(t -> t.clearFilteredDishes());
        // cycle through proposal order
        Integer[] cycle = proposal.getAttemptOrder();
        for (int i = 0; i < cycle.length; i++) {
            RawSlotResult rawResult = rawResults.get(cycle[i]);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(settings.getDishCountPerSlot());
            proposal.setDishMatches(i, dishMatches);
            for (int j = i + 1; j < cycle.length; j++) {
                rawResults.get(cycle[j]).addDishesToFilter(dishMatches);
            }
        }
        proposal.finalizeResults();
    }

    private List<ProposalAttempt> getProposalApproaches(ApproachType approachType, int slotcount, TargetProposalSettings settings) {


        List<Integer[]> approachOrders = AttemptGenerator.getProposalOrders(approachType, slotcount, settings.getProposalCount());

        List<ProposalAttempt> proposalAttempts = new ArrayList<>();
        for (Integer[] order : approachOrders) {
            ProposalAttempt proposalAttempt = new ProposalAttempt(order);
            proposalAttempts.add(proposalAttempt);
        }
        return proposalAttempts;
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

    private ApproachType getApproachType(int slotcount) {
        return null;
    }

    private List<AttemptResult> getAttemptResults(TargetProposalSettings settings, int slotcount, List<RawSlotResult> rawResults, int dishCountPerSlot) {
/*

        List<ProposalAttempt> proposals = getProposalApproaches(settings.getApproachType(), slotcount, settings.getProposalCount());

        //process each proposal, collecting results
        List<AttemptResult> results = new ArrayList<>();
        for (ProposalAttempt proposal : proposals) {
            // run single proposal
            //AttemptResult result = processSingleProposal(proposal, rawResults, settings.getDishCountPerSlot());
            //results.add(result);
        }


        return results;
 */
        return null;
    }

}
