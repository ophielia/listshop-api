package com.meg.atable.service.impl;

import com.meg.atable.data.entity.*;
import com.meg.atable.service.*;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by margaretmartin on 08/06/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
//@MockBean(DishSearchService.class)
public class SearchProposalProcessorImplTest {

@MockBean
private TagStructureService tagStructureServiceImpl;

    @MockBean
    private DishSearchService dishSearchService;

    @Autowired
    private SearchProposalProcessorImpl processor;

    @Test
    public void processProposal() throws Exception {
        TargetEntity target = getDummyTarget();
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = getProposalForTarget(target);
        testRequest.setTarget(target);

        // get tag structure dummy results
        Set<Long> allTags = target.getAllTagIds();
        //MM change to mock call
        Mockito.when(tagStructureServiceImpl.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long,List<Long>>());
        //Map<Long, List<Long>> searchGroups = tagStructureServiceImpl.getSearchGroupsForTagIds(allTags);

        // get raw results
        Map<String,Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = makeDummySearchResults(slot,dishTagMatches, target.getTagIdsAsSet(),target.getTagIdsAsSet().size(),20, 3,false, true);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(TestConstants.USER_3_ID,slot,any(Integer.class),
                    any(List.class),any(Map.class),any(List.class)))
            .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal( testRequest);

        Assert.assertNotNull(testResult);
    }

    private List<DishTagSearchResult> makeDummySearchResults(TargetSlotEntity slot, Map<String, Boolean> dishTagMatches,Set<String> targetTagIds,int targetIdCount,
                                                             int resultCount, int emptyCount,boolean isOffset, boolean matchAll) {
        List<DishTagSearchResult> results = new ArrayList<>();
        List<String> allTags = new ArrayList<>();
        allTags.addAll(targetTagIds);
        allTags.addAll(slot.getTagIdsAsList());

        int offset = 1;
        if (isOffset) {
        offset = (slot.getSlotOrder() * 5)+1;
    }
        for (int i=0;i<resultCount-emptyCount;i++) {
Long dummyDishId = (long)i + offset;
            DishTagSearchResult result = new DishTagSearchResult(dummyDishId, null,targetIdCount,allTags.size());
            for (int j=0;j<allTags.size();j++) {
                boolean found = doesTagMatch(dummyDishId,allTags.get(j),dishTagMatches,matchAll);
                result.addTagResult(j,found?1:0);
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
        boolean found = test < 50;
        dishTagMatches.put(key,found);
        return found;

    }

    private TargetEntity getDummyTarget() {
        TargetEntity target = new TargetEntity();

        List<TargetSlotEntity> slots = new ArrayList<>();
        TargetSlotEntity slot1 = new TargetSlotEntity();
        slot1.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot1.addTagId(1L);
        slots.add(slot1);

        TargetSlotEntity slot2 = new TargetSlotEntity();
        slot2.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot2.addTagId(2L);
        slots.add(slot2);

        TargetSlotEntity slot3 = new TargetSlotEntity();
        slot3.setSlotDishTagId(TestConstants.TAG_SIDE_DISH);
        slot3.addTagId(3L);
        slots.add(slot3);

        TargetSlotEntity slot4 = new TargetSlotEntity();
        slot4.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
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
        for (TargetSlotEntity slot: target.getSlots()) {
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
        for (int i = 1; i< counter ; i++) {
            Random random = new Random();
            int value = random.nextInt(100);
            dishIds.add(Long.valueOf(value));
        }
        return dishIds;
    }




}