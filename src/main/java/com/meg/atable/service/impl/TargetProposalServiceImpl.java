package com.meg.atable.service.impl;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.api.model.SortDirection;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.*;
import com.meg.atable.service.*;
import com.meg.atable.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
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
    private ProposalContextRepository proposalContextRepository;

    @Autowired
    private ProposalContextApproachRepository proposalContextApproachRepository;

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
        ProposalContextEntity context = buildProposalContext(slotcount);

        // get database info for slots
        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, context, null);

        // get list of approaches (order in which slots are assembled)
        List<ProposalAttempt> approaches = getProposalAttempts(context, slotcount);

        // process each proposal attempt
        processProposals(approaches, rawResults, context);
        //List<AttemptResult> results = getAttemptResults(settings, slotcount, rawResults, settings.getDishCountPerSlot());

        // sort for best results
        boolean byMedian = false;
        approaches = sortForBestResults(approaches);

        // assign best approach to new proposal
        if (approaches.get(0) != null) {
            TargetProposalEntity proposal = createProposalFromAttempt(target, rawResults, approaches.get(0), context);
            proposal.setUserId(user.getId());
            proposal = targetProposalRepository.save(proposal);

            context = proposalContextRepository.save(context);
            List<ProposalContextApproachEntity> persistedApproaches = buildProposalContextApproaches(context, approaches);
            context.setContextApproaches(persistedApproaches);
            context.setProposalId(proposal.getProposalId());
            context.setCurrentAttemptIndex(0);
            proposalContextRepository.save(context);

            target.setProposalId(proposal.getProposalId());
            targetService.save(target);
            return proposal;
        }
        return null;
    }

    private List<ProposalContextApproachEntity> buildProposalContextApproaches(ProposalContextEntity context, List<ProposalAttempt> approaches) {
        if (approaches == null) {
            return null;
        }
        List<ProposalContextApproachEntity> contextApproachEntities = new ArrayList<>();
        int i = 0;
        for (ProposalAttempt attempt : approaches) {
            ProposalContextApproachEntity contextApproachEntity = new ProposalContextApproachEntity();
            contextApproachEntity.setProposalContext(context);
            contextApproachEntity.setSortKey(i);
            contextApproachEntity.setApproachOrder(attempt.getAttemptOrderAsString(";"));
            contextApproachEntities.add(contextApproachEntity);
            i++;
        }

        return proposalContextApproachRepository.save(contextApproachEntities);
    }

    @Override
    public TargetProposalEntity getTargetProposalById(String name, Long proposalId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        TargetProposalEntity proposal = targetProposalRepository.findOne(proposalId);
        if (proposal.getUserId() == null || !proposal.getUserId().equals(user.getId())) {
            return null;
        }

        proposal.getProposalSlots().sort(Comparator.comparing(TargetProposalSlotEntity::getSlotOrder));
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
    public void refreshTargetProposal(String name, Long proposalId, SortDirection sortDirection) {
        UserAccountEntity user = userService.getUserByUserName(name);
        TargetProposalEntity proposal = getTargetProposalById(name, proposalId);

        if (proposal == null) {
            return;
        }

        // get the target
        TargetEntity target = targetService.getTargetById(name, proposal.getForTargetId());
        if (target.getSlots() == null || target.getSlots().isEmpty()) {
            return;
        }

        // get the context
        ProposalContextEntity context = proposalContextRepository.findByProposalId(proposal.getProposalId());
        if (context == null) {
            return;
        }

        // determine if proposal can be refreshed
        if (context.getRefreshFlag().equals(proposal.generateRefreshFlag())) {
            // no changes since last proposal generated.  can just move to next approach;
            doRefreshProposal(target, proposal, context, sortDirection);
        } else {
            doRegenerateProposal(target, proposal, context, sortDirection);
        }
    }

    private void doRefreshProposal(TargetEntity target, TargetProposalEntity proposal, ProposalContextEntity context, SortDirection sortDirection) {
        Map<Long, Long> slotToSelected = proposal.getSelectedDishIdsBySlot();

        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, context, null);

        // insert selectedDishes into rawResults
        for (RawSlotResult result : rawResults) {
            result.addExcludesAndPresets(slotToSelected);
        }

        // get list of approaches (order in which slots are assembled)
        int refreshIndex = sortDirection == SortDirection.UP ? context.getCurrentAttemptIndex() + 1 : context.getCurrentAttemptIndex() - 1;
        if (refreshIndex < 0) {
            refreshIndex = refreshIndex + context.getProposalCount();
        } else if (refreshIndex >= context.getProposalCount()) {
            refreshIndex = refreshIndex - context.getProposalCount();
        }
        ProposalAttempt attempt = buildNextProposalAttempt(context, refreshIndex);

        proposal = clearDishesFromProposal(proposal);
        proposal = setResultsInProposal(proposal, rawResults, attempt, context, slotToSelected);
        context.setRefreshFlag(proposal.generateRefreshFlag());
        context.setCurrentAttemptIndex(refreshIndex);

        // save changes
        proposal.setTargetName("idx;" + context.getCurrentAttemptIndex() + ";");
        proposal = targetProposalRepository.save(proposal);
        target.setProposalId(proposal.getProposalId());
        targetService.save(target);
        proposalContextRepository.save(context);


    }

    private ProposalAttempt buildNextProposalAttempt(ProposalContextEntity context, int index) {
        // gather info
        List<ProposalContextApproachEntity> attempts = context.getSlots();
        int approachCount = attempts.size();

        ProposalContextApproachEntity nextDisplay = attempts.stream().filter(a -> a.getSortKey().intValue() == index)
                .findFirst().get();

        // make ProposalAttempt
        String[] stringOrder = nextDisplay.getApproachOrder().split(";");
        Integer[] order = new Integer[stringOrder.length];
        for (int i = 0; i < stringOrder.length; i++) {
            order[i] = new Integer(stringOrder[i]);
        }

        return new ProposalAttempt(order);
    }

    private void doRegenerateProposal(TargetEntity target, TargetProposalEntity proposal, ProposalContextEntity context, SortDirection sortDirection) {
        // determine maximum dishes returned per slot, slot count
        int slotcount = target.getSlots().size();

        Map<Long, Long> slotToSelected = proposal.getSelectedDishIdsBySlot();

        // results are sorted from least found (weakest) to most found
        List<RawSlotResult> rawResults = retrieveRawResults(target, context, null);

        // insert selectedDishes into rawResults
        for (RawSlotResult result : rawResults) {
            result.addExcludesAndPresets(slotToSelected);
        }


        // get list of approaches (order in which slots are assembled)
        List<ProposalAttempt> approaches = getProposalAttempts(context, slotcount);

        // process each proposal attempt
        processProposals(approaches, rawResults, context);
        //List<AttemptResult> results = getAttemptResults(settings, slotcount, rawResults, settings.getDishCountPerSlot());

        // sort for best results
        boolean byMedian = false;
        approaches = sortForBestResults(approaches);

        // assign best approach to new proposal
        if (approaches.get(0) != null) {
            proposal = clearDishesFromProposal(proposal);
            proposal = setResultsInProposal(proposal, rawResults, approaches.get(0), context, slotToSelected);
            proposal = targetProposalRepository.save(proposal);

            context = proposalContextRepository.save(context);
            List<ProposalContextApproachEntity> persistedApproaches = buildProposalContextApproaches(context, approaches);
            context.setContextApproaches(persistedApproaches);
            context.setProposalId(proposal.getProposalId());
            context.setCurrentAttemptIndex(0);
            proposalContextRepository.save(context);

            target.setProposalId(proposal.getProposalId());
            targetService.save(target);
        }

    }


    private TargetProposalEntity clearDishesFromProposal(TargetProposalEntity proposal) {
        List<Long> slotIds = proposal.getProposalSlots().stream().map(s -> s.getTargetSlotId()).collect(Collectors.toList());
        targetProposalDishRepository.deleteDishesForSlots(slotIds);
        targetProposalDishRepository.flush();
        return targetProposalRepository.findOne(proposal.getProposalId());
    }

    private TargetProposalSlotEntity clearDishesFromProposalSlot(TargetProposalSlotEntity slot) {
        List<Long> slotIds = Collections.singletonList(slot.getSlotId());
        targetProposalDishRepository.deleteDishesForSlots(slotIds);
        targetProposalDishRepository.flush();
        return targetProposalSlotRepository.findOne(slot.getSlotId());
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

        return proposalEntity.getProposalSlots().stream()
                .filter(s -> s.getSlotId().longValue() == slotidcheck)
                .findFirst().get();
    }


    private TargetSlotEntity getSlotFromTarget(TargetEntity target, Long slotId) {
        if (target == null ||
                target.getSlots() == null) {
            return null;
        }
        long slotidcheck = slotId.longValue();

        return target.getSlots().stream()
                .filter(s -> s.getId().longValue() == slotidcheck)
                .findFirst().get();
    }

    private TargetProposalSlotEntity getSlotFromProposalByTargetId(TargetProposalEntity proposalEntity, Long slotId) {
        if (proposalEntity == null ||
                proposalEntity.getProposalSlots() == null) {
            return null;
        }
        long slotidcheck = slotId.longValue();

        return proposalEntity.getProposalSlots().stream()
                .filter(s -> s.getTargetSlotId().longValue() == slotidcheck)
                .findFirst().get();
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
        targetProposalRepository.save(proposalEntity);
    }

    @Override
    public void showMoreProposalSlotOptions(String name, Long proposalId, Long slotId) {
        UserAccountEntity user = userService.getUserByUserName(name);
        TargetProposalEntity proposal = getTargetProposalById(name, proposalId);

        if (proposal == null) {
            return;
        }

        // get the target
        TargetEntity target = targetService.getTargetById(name, proposal.getForTargetId());
        if (target.getSlots() == null || target.getSlots().isEmpty()) {
            return;
        }

        // get the context
        ProposalContextEntity context = proposalContextRepository.findByProposalId(proposal.getProposalId());
        if (context == null) {
            return;
        }

        // get slot which should be added to
        TargetProposalSlotEntity slot = getSlotFromProposal(proposal, slotId);
        TargetSlotEntity targetSlot = getSlotFromTarget(target, slot.getTargetSlotId());

        // get raw results for slot
        List<String> targetTagIds = target.getTagIdsAsList();
        List<String> tagListForSlot = new ArrayList<>();
        tagListForSlot.addAll(targetTagIds);
        tagListForSlot.addAll(targetSlot.getTagIdsAsList());

        // query db
        RawSlotResult rawSlotResult = retrieveSingleSlotResult(targetSlot, user.getId(), targetTagIds, context);

        // add all existing dish ids to filter
        List<Long> dishIdsInProposal = proposal.getAllDishIds();
        rawSlotResult.addDishIdsToFilter(dishIdsInProposal);

        // add these raw results (top dishcount) to proposal slot
        List<DishTagSearchResult> topDishResults = rawSlotResult.getFilteredMatches(context.getDishCountPerSlot());
        slot = clearDishesFromProposalSlot(slot);
        setDishResultsInProposalSlot(topDishResults, slot, rawSlotResult.getTagListForSlot());

        // save proposal
        proposal = targetProposalRepository.save(proposal);
        target.setProposalId(proposal.getProposalId());
    }


    private TargetProposalEntity createProposalFromAttempt(TargetEntity target, List<RawSlotResult> rawResults, ProposalAttempt result, ProposalContextEntity context) {
        // make a new TargetProposalEntity
        TargetProposalEntity proposalEntity = new TargetProposalEntity(target);
        // set additional information in proposal - regenerateOnRefresh, currentProposalIndex, and proposal list
        context.setCurrentAttemptIndex(0);

        // save proposal (in order to assign ids)
        proposalEntity = targetProposalRepository.save(proposalEntity);

        // process ProposalAttempt to fill raw results
        processSingleProposal(result, rawResults, context);

        // get result and fill slots
        Integer[] order = result.getAttemptOrder();
        for (int i = 0; i < order.length; i++) {
            // get rawResult for order
            RawSlotResult rawResult = rawResults.get(i);

            TargetSlotEntity targetSlot = target.getSlots().stream().filter(s -> s.getId().longValue() == rawResult.getSlotId()).findFirst().get();
            TargetProposalSlotEntity proposalSlotEntity = new TargetProposalSlotEntity(proposalEntity, targetSlot);
            proposalSlotEntity = targetProposalSlotRepository.save(proposalSlotEntity);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(context.getDishCountPerSlot());
            setDishResultsInProposalSlot(dishMatches, proposalSlotEntity, rawResult.getTagListForSlot());

            proposalEntity.addSlot(proposalSlotEntity);
        }

        // set refresh flag
        context.setRefreshFlag(proposalEntity.generateRefreshFlag());
        return proposalEntity;
    }

    private TargetProposalEntity setResultsInProposal(TargetProposalEntity proposal, List<RawSlotResult> rawResults, ProposalAttempt result, ProposalContextEntity context, Map<Long, Long> selectedBySlot) {
        // make a new TargetProposalEntity
        context.setCurrentAttemptIndex(0);

        // process ProposalAttempt to fill raw results
        processSingleProposal(result, rawResults, context);

        // get result and fill slots
        Integer[] order = result.getAttemptOrder();
        for (int i = 0; i < order.length; i++) {
            // get rawResult for order
            RawSlotResult rawResult = rawResults.get(order[i]);

            TargetProposalSlotEntity proposalSlotEntity = getSlotFromProposalByTargetId(proposal, rawResult.getSlotId());
            // clear existing dishes
            targetProposalDishRepository.delete(proposalSlotEntity.getDishSlotList());
            proposalSlotEntity.setDishSlotList(null);
            proposalSlotEntity = targetProposalSlotRepository.save(proposalSlotEntity);
            proposalSlotEntity.setDishSlotList(new ArrayList<>());


            if (selectedBySlot.containsKey(proposalSlotEntity.getTargetSlotId())) {
                proposalSlotEntity.setSelectedDishIndex(0);
            } else {
                proposalSlotEntity.setSelectedDishIndex(-1);
            }

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(context.getDishCountPerSlot());
            setDishResultsInProposalSlot(dishMatches, proposalSlotEntity, rawResult.getTagListForSlot());
            /*for (DishTagSearchResult singleDish : dishMatches) {
                TargetProposalDishEntity dish = new TargetProposalDishEntity();
                dish.setDishId(singleDish.getDishId());
                List<String> matchedIds = singleDish.getMatchedTagIds(rawResult.getTagListForSlot());
                dish.setMatchedTagIds(String.join(";", matchedIds));
                dish.setTargetProposalSlot(proposalSlotEntity);
                dish = targetProposalDishRepository.save(dish);
                proposalSlotEntity.addDish(dish);
            }*/

        }

        // set refresh flag
        context.setRefreshFlag(proposal.generateRefreshFlag());
        return proposal;
    }


    private void setDishResultsInProposalSlot(List<DishTagSearchResult> dishMatches,
                                              TargetProposalSlotEntity proposalSlotEntity,
                                              List<String> tagListForSlot) {

        for (DishTagSearchResult singleDish : dishMatches) {
            TargetProposalDishEntity dish = new TargetProposalDishEntity();
            dish.setDishId(singleDish.getDishId());
            List<String> matchedIds = singleDish.getMatchedTagIds(tagListForSlot);
            dish.setMatchedTagIds(String.join(";", matchedIds));
            dish.setTargetProposalSlot(proposalSlotEntity);
            dish = targetProposalDishRepository.save(dish);
            proposalSlotEntity.addDish(dish);
        }


    }


    private ProposalContextEntity buildProposalContext(int slotcount) {
        ProposalContextEntity context = new ProposalContextEntity();
        context.setMaximumEmpties(5);
        context.setDishCountPerSlot(5);
        if (slotcount < 3) {
            context.setApproachType(ApproachType.WHEEL);
            context.setProposalCount(slotcount);
            return context;
        }
        context.setApproachType(ApproachType.WHEEL_MIXED);
        context.setProposalCount(Math.min(slotcount + 1, 10));
        return context;
    }

    private List<ProposalAttempt> sortForBestResults(List<ProposalAttempt> results) {

        results.sort(Comparator.comparing(ProposalAttempt::getHealthIndexMedian)
                .thenComparing(ProposalAttempt::getHealthIndexAverage).reversed());
        return results;
    }

    private List<ProposalAttempt> processProposals(List<ProposalAttempt> proposals, List<RawSlotResult> rawResults, ProposalContextEntity context) {
        for (ProposalAttempt proposal : proposals) {
            // run single proposal
            processSingleProposal(proposal, rawResults, context);
        }

        return proposals;
    }

    private void processSingleProposal(ProposalAttempt proposal, List<RawSlotResult> rawResults, ProposalContextEntity context) {
        // clear all filters
        rawResults.forEach(t -> t.clearFilteredDishes());
        // cycle through proposal order
        Integer[] cycle = proposal.getAttemptOrder();
        for (int i = 0; i < cycle.length; i++) {
            RawSlotResult rawResult = rawResults.get(cycle[i]);

            List<DishTagSearchResult> dishMatches = rawResult.getFilteredMatches(context.getDishCountPerSlot());
            proposal.setDishMatches(i, dishMatches);
            for (int j = i + 1; j < cycle.length; j++) {
                rawResults.get(cycle[j]).addDishesToFilter(dishMatches);
            }
        }
        proposal.finalizeResults();
    }

    private List<ProposalAttempt> getProposalAttempts(ProposalContextEntity context, int slotcount) {
        ApproachType approachType = context.getApproachType();

        List<Integer[]> approachOrders = AttemptGenerator.getProposalOrders(approachType, slotcount, context.getProposalCount());

        List<ProposalAttempt> proposalAttempts = new ArrayList<>();
        for (Integer[] order : approachOrders) {
            ProposalAttempt proposalAttempt = new ProposalAttempt(order);
            proposalAttempts.add(proposalAttempt);
        }
        return proposalAttempts;
    }

    private List<RawSlotResult> retrieveRawResults(TargetEntity target, ProposalContextEntity context, List<Long> dishExcludeList) {
        int maxempties = context.getMaximumEmpties();
        // get target tags
        List<String> targetTagIds = target.getTagIdsAsList();
        List<TargetSlotEntity> targetSlots = target.getSlots();
        Long userId = target.getUserId();

        // retrieve results for each slot
        List<RawSlotResult> resultList = new ArrayList<>();
        for (TargetSlotEntity slot : targetSlots) {
            RawSlotResult rawSlotResult = retrieveSingleSlotResult(slot, userId, target.getTagIdsAsList(), context);
            resultList.add(rawSlotResult);
        }

        // sort results by total dishes found
        resultList.sort(Comparator.comparing(RawSlotResult::getRawMatchCount));
        return resultList;
    }

    private RawSlotResult retrieveSingleSlotResult(TargetSlotEntity slot, Long userId, List<String> targetTagIds, ProposalContextEntity context) {
        List<String> tagListForSlot = new ArrayList<>();
        tagListForSlot.addAll(targetTagIds);
        tagListForSlot.addAll(slot.getTagIdsAsList());

        // query db
        List<DishTagSearchResult> dishResults = dishSearchService.retrieveDishResultsForTags(userId, slot.getSlotDishTagId(), targetTagIds.size(), tagListForSlot);
        List<DishTagSearchResult> matches = new ArrayList<>();
        List<DishTagSearchResult> targetMatches = new ArrayList<>();
        List<DishTagSearchResult> emptyMatches = new ArrayList<>();
        dishResults
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
        if (emptyMatches.size() > context.getMaximumEmpties()) {
            end = context.getMaximumEmpties();

        }
        // a word about sorting - the results are sorted by last_added date from the database.  Additional
        // sorting by match counts (full and slot) is done within RawSlotResults
        return new RawSlotResult(slot.getId(), matches, targetMatches, emptyMatches.subList(0, end), tagListForSlot);

    }


}
