package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.api.model.ApproachType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.service.AttemptGenerator;
import com.meg.listshop.lmt.service.DishTagSearchResult;
import com.meg.listshop.test.TestConstants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 15/06/2018.
 */
public class ProcessorTestUtils {

    protected static TargetEntity getDummyTarget(int slotCount, int tagSlotCount, int tagTargetCount) {
        TargetEntity target = new TargetEntity();
        List<TargetSlotEntity> slots = new ArrayList<>();

        int tagCounter = 1;
        for (int i = 0; i < slotCount; i++) {
            TargetSlotEntity slot = new TargetSlotEntity();
            slot.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
            for (int j = 0; j < tagSlotCount; j++) {
                slot.addTagId((long) tagCounter);
                tagCounter++;
            }
            slot.setSlotOrder(i);
            slots.add(slot);
        }

        target.setSlots(slots);
        for (int i = 0; i < tagTargetCount; i++) {
            target.addTargetTagId((long) tagCounter);
            tagCounter++;
        }

        return target;
    }

    protected static List<DishTagSearchResult> makeDummySearchResults(TargetSlotEntity slot, Map<String, Boolean> dishTagMatches, Set<String> targetTagIds, int targetIdCount,
                                                                      int resultCount, int emptyCount, boolean isOffset, boolean matchAll, List<Long> filter) {
        List<DishTagSearchResult> results = new ArrayList<>();
        List<String> allTags = new ArrayList<>();
        allTags.addAll(targetTagIds);
        allTags.addAll(slot.getTagIdsAsList());

        int offset = 1;
        if (isOffset) {
            offset = (slot.getSlotOrder() * 2) + 1;
        }
        int i = -1;
        while (i < resultCount - emptyCount) {
            i++;
            Long dummyDishId = (long) i + offset;
            if (filter.contains(dummyDishId)) {
                continue;
            }
            DishTagSearchResult result = new DishTagSearchResult(dummyDishId, null, targetIdCount, allTags.size());
            for (int j = 0; j < allTags.size(); j++) {
                boolean found = doesTagMatch(dummyDishId, allTags.get(j), dishTagMatches, matchAll);
                result.addTagResult(j, found ? 1 : 0);
            }
            results.add(result);

        }

        i = resultCount - emptyCount - 1;
        while (i < resultCount) {
            i++;
            Long dummyDishId = (long) i + offset;
            DishTagSearchResult result = new DishTagSearchResult(dummyDishId, null, targetIdCount, allTags.size());
            for (int j = 0; j < allTags.size(); j++) {
                boolean found = false;
                result.addTagResult(j, found ? 1 : 0);
            }
            results.add(result);

        }

        return results;
    }

    protected static boolean doesTagMatch(Long dummyDishId, String tagId, Map<String, Boolean> dishTagMatches, boolean matchAll) {
        if (matchAll) {
            return true;
        }
        String key = dummyDishId + "!" + tagId;
        if (dishTagMatches.containsKey(key)) {
            return dishTagMatches.get(key);
        }
        Random random = new Random();
        int test = random.nextInt(100);
        boolean found = test < 40;
        dishTagMatches.put(key, found);
        return found;

    }


    protected static ProposalEntity getProposalForTarget(TargetEntity target) {
        ProposalEntity proposal = new ProposalEntity();
        List<ProposalSlotEntity> proposals = new ArrayList<ProposalSlotEntity>();
        for (TargetSlotEntity slot : target.getSlots()) {
            ProposalSlotEntity proposalSlot = new ProposalSlotEntity();
            proposalSlot.setSlotNumber(slot.getSlotOrder());
            List<DishSlotEntity> dishSlots = new ArrayList<>();
            List<Long> sampleDishes = getRandomDishIds(5);
            for (Long dishId : sampleDishes) {
                DishSlotEntity dishSlot = new DishSlotEntity();
                dishSlot.setDishId(dishId);
                dishSlots.add(dishSlot);
            }
            proposalSlot.setDishSlots(dishSlots);
            proposals.add(proposalSlot);
        }
        proposal.setSlots(proposals);
        return proposal;
    }

    protected static List<Long> getRandomDishIds(int counter) {
        List<Long> dishIds = new ArrayList<>();
        for (int i = 1; i < counter; i++) {
            Random random = new Random();
            int value = random.nextInt(100);
            dishIds.add(Long.valueOf(value));
        }
        return dishIds;
    }

    public static MealPlanEntity getDummyMealPlan(int dishSlotCount) {
        MealPlanEntity mealPlan = new MealPlanEntity();
        List<Long> dishIds = getRandomDishIds(dishSlotCount);
        List<SlotEntity> slots = new ArrayList<>();
        for (Long dishId : dishIds) {
            DishEntity dish = new DishEntity();
            dish.setId(dishId);
            SlotEntity dishSlot = new SlotEntity();
            dishSlot.setDish(dish);
            slots.add(dishSlot);
        }
        mealPlan.setSlots(slots);
        return mealPlan;
    }

    public static ProposalContextEntity getDummyContext(TargetEntity target, int proposalCount, int selectedIndex) {
        // create context
        // don't worry about links to target / proposal
        ProposalContextEntity context = new ProposalContextEntity();

        // fill context approaches
        List<Integer[]> approachOrders = getDummyApproachOrders(target,5);
        List<ContextApproachEntity> approaches = new ArrayList<>();
        for (int i = 0; i < proposalCount; i++) {
            ContextApproachEntity approach = new ContextApproachEntity();
            approach.setContext(context);
            approach.setApproachNumber(i);
            List<String> instructionList = Arrays.stream(approachOrders.get(i)).map(String::valueOf).collect(Collectors.toList());
            approach.setInstructions(FlatStringUtils.flattenListToString(instructionList,";"));
            approaches.add(approach);
        }
        context.setApproaches(approaches);
        context.setCurrentApproachIndex(selectedIndex);
        return context;
    }

    private static List<Integer[]> getDummyApproachOrders(TargetEntity target, int proposalCount) {

        Map<Integer, Integer> dummyIndexToSlot = new HashMap<>();
        for (int i = 0; i < proposalCount; i++) {
            dummyIndexToSlot.put(i, i);
        }

        return AttemptGenerator.getProposalOrders(ApproachType.WHEEL_MIXED, target.getSlots().size(), proposalCount, dummyIndexToSlot);
    }

    public static ProposalContextEntity getDummyContext(int selectedIndex,TargetEntity target, ProposalEntity proposalEntity) {
        // create context
        // don't worry about links to target / proposal
        ProposalContextEntity context = new ProposalContextEntity();

        // fill context approaches
        List<Integer> searchSlots = new ArrayList<>();
        for (ProposalSlotEntity proposalSlot : proposalEntity.getSlots()) {
            if (proposalSlot.getPickedDishId()!=null) {
                continue;
            }
            searchSlots.add(proposalSlot.getSlotNumber());
        }

        Map<Integer, Integer> dummyIndexToSlot = new HashMap<>();
        for (int i = 0; i < searchSlots.size(); i++) {
            dummyIndexToSlot.put(i, searchSlots.get(i));
        }

        int proposalCount = Math.min(5, searchSlots.size());
        List<Integer[]> approachOrders = AttemptGenerator.getProposalOrders(ApproachType.WHEEL_MIXED, searchSlots.size(), proposalCount, dummyIndexToSlot);

        List<ContextApproachEntity> approaches = new ArrayList<>();
        for (int i = 0; i < proposalCount; i++) {
            ContextApproachEntity approach = new ContextApproachEntity();
            approach.setContext(context);
            approach.setApproachNumber(i);
            List<String> instructionList = Arrays.stream(approachOrders.get(i)).map(String::valueOf).collect(Collectors.toList());
            approach.setInstructions(FlatStringUtils.flattenListToString(instructionList,";"));
            approaches.add(approach);
        }
        context.setApproaches(approaches);
        context.setCurrentApproachIndex(selectedIndex);
        return context;
    }
}
