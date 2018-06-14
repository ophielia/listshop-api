package com.meg.atable.service.impl;

import com.meg.atable.data.entity.*;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.DishTagSearchResult;
import com.meg.atable.service.ProcessResult;
import com.meg.atable.service.tag.TagStructureService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Created by margaretmartin on 08/06/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class SearchProposalProcessorImplTest {


    @Autowired
    private SearchProposalProcessorImpl processor;

    @MockBean
    private TagStructureService tagStructureService;

    @MockBean
    private DishSearchService dishSearchService;


    @Test
    public void processProposal_NoExistingProposal() throws Exception {
        TargetEntity target = getDummyTarget();
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false,new ArrayList<>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                    any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(4, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            Assert.assertEquals(5, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(0, testResult.getCurrentApproach());
        // resultApproach > 1
        Assert.assertTrue(testResult.getResultApproaches().size() > 1);

    }


    @Test
    public void processProposal_ExistingProposal() throws Exception {
        TargetEntity target = getDummyTarget();
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = getProposalForTarget(target);
        testRequest.setTarget(target);


        // set picked dish in first and last slots
        Map<Integer, ProposalSlotEntity> proposalSlotHash = new HashMap<>();
        proposalEntity.getSlots().stream().forEach(t -> proposalSlotHash.put(t.getSlotNumber(), t));
        ProposalSlotEntity firstSlot = proposalSlotHash.get(0);
        Long firstPickedId = firstSlot.getDishSlots().get(0).getDishId();
        firstSlot.setPickedDishId(firstPickedId);
        ProposalSlotEntity lastSlot = proposalSlotHash.get(3);
        Long lastPickedId = lastSlot.getDishSlots().get(0).getDishId();
        lastSlot.setPickedDishId(lastPickedId);
        List<Long> sqlFilter = new ArrayList<>();
        List<Integer> pickedSlots = new ArrayList<>();
        sqlFilter.add(firstPickedId);
        sqlFilter.add(lastPickedId);
        pickedSlots.add(firstSlot.getSlotNumber());
        pickedSlots.add(lastSlot.getSlotNumber());
        testRequest.setProposal(proposalEntity);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, sqlFilter);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                    any(List.class), any(Map.class), eq(sqlFilter)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(4, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            int dishSlotCount = pickedSlots.contains(slot.getSlotNumber())?4:5;
            Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(0, testResult.getCurrentApproach());
        // resultApproach > 0   - random results with 2 slots lead to valid one result only sometimes.
        Assert.assertTrue(testResult.getResultApproaches().size() > 0);

    }


    private List<DishTagSearchResult> makeDummySearchResults(TargetSlotEntity slot, Map<String, Boolean> dishTagMatches, Set<String> targetTagIds, int targetIdCount,
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
        while (i<resultCount-emptyCount) {
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

        i = resultCount - emptyCount-1;
        while ( i < resultCount) {
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

    private boolean doesTagMatch(Long dummyDishId, String tagId, Map<String, Boolean> dishTagMatches, boolean matchAll) {
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

    private TargetEntity getDummyTarget() {
        TargetEntity target = new TargetEntity();

        List<TargetSlotEntity> slots = new ArrayList<>();
        TargetSlotEntity slot1 = new TargetSlotEntity();
        slot1.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot1.addTagId(1L);
        slot1.setSlotOrder(0);
        slots.add(slot1);

        TargetSlotEntity slot2 = new TargetSlotEntity();
        slot2.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot2.setSlotOrder(1);
        slot2.addTagId(2L);
        slots.add(slot2);

        TargetSlotEntity slot3 = new TargetSlotEntity();
        slot3.setSlotDishTagId(TestConstants.TAG_SIDE_DISH);
        slot3.setSlotOrder(2);
        slot3.addTagId(3L);
        slots.add(slot3);

        TargetSlotEntity slot4 = new TargetSlotEntity();
        slot4.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot4.setSlotOrder(3);
        slot4.addTagId(4L);
        slots.add(slot4);

        target.setSlots(slots);
        target.addTargetTagId(10L);
        target.addTargetTagId(11L);
        target.addTargetTagId(12L);

        return target;
    }

    private ProposalEntity getProposalForTarget(TargetEntity target) {
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

    private List<Long> getRandomDishIds(int counter) {
        List<Long> dishIds = new ArrayList<>();
        for (int i = 1; i < counter; i++) {
            Random random = new Random();
            int value = random.nextInt(100);
            dishIds.add(Long.valueOf(value));
        }
        return dishIds;
    }


}