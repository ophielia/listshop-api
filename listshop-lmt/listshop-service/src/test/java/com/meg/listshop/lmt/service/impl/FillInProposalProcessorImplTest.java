package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.dish.DishSearchService;
import com.meg.listshop.lmt.dish.DishTagSearchResult;
import com.meg.listshop.lmt.service.proposal.ProcessResult;
import com.meg.listshop.lmt.service.proposal.ProposalRequest;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
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
public class FillInProposalProcessorImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private FillInProposalProcessorImpl processor;

    @MockBean
    private TagStructureService tagStructureService;

    @MockBean
    private DishSearchService dishSearchService;


    @Test
    public void testFillInProposal_Picked() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setTarget(target);


        // set picked dish in first and last slots
        Map<Integer, ProposalSlotEntity> proposalSlotHash = new HashMap<>();
        proposalEntity.getSlots().forEach(t -> proposalSlotHash.put(t.getSlotNumber(), t));
        ProposalSlotEntity firstSlot = proposalSlotHash.get(0);
        Long firstPickedId = firstSlot.getDishSlots().get(0).getDishId();
        firstSlot.setPickedDishId(firstPickedId);
        ProposalSlotEntity lastSlot = proposalSlotHash.get(3);
        Long lastPickedId = lastSlot.getDishSlots().get(0).getDishId();
        lastSlot.setPickedDishId(lastPickedId);
        List<Long> sqlFilter = new ArrayList<>();
        sqlFilter.add(firstPickedId);
        sqlFilter.add(lastPickedId);
        testRequest.setProposal(proposalEntity);
        ProposalSlotEntity fillInProposalSlot = proposalSlotHash.get(1);
        int origCount = fillInProposalSlot.getDishSlots().size();


        // set context
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(0, target, proposalEntity);
        testRequest.setContext(context);
        int origApproach = context.getCurrentApproachIndex();

        // set fill in tag slot - not a picked one
        testRequest.setFillInSlotNumber(1);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, sqlFilter);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), eq(sqlFilter)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 1 slots with 9 dishes
        Assert.assertEquals(1, testResult.getResultSlots().size());
        ProposalSlotEntity slot = testResult.getResultSlots().get(0);
        // at least one tag match
        boolean tagMatch = false;
        int dishSlotCount = origCount + 5;
        Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
        for (DishSlotEntity dishSlot : slot.getDishSlots()) {
            tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
            if (tagMatch) {
                break;
            }

        }
        Assert.assertTrue(tagMatch);


        // currentApproach is 1
        Assert.assertEquals(origApproach, testResult.getCurrentApproach());
    }

    @Test
    @Ignore
    public void testFillInProposal_PickedToFillIn() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setTarget(target);


        // set picked dish in first and last slots
        Map<Integer, ProposalSlotEntity> proposalSlotHash = new HashMap<>();
        proposalEntity.getSlots().forEach(t -> proposalSlotHash.put(t.getSlotNumber(), t));
        ProposalSlotEntity firstSlot = proposalSlotHash.get(0);
        Long firstPickedId = firstSlot.getDishSlots().get(0).getDishId();
        firstSlot.setPickedDishId(firstPickedId);
        ProposalSlotEntity lastSlot = proposalSlotHash.get(3);
        Long lastPickedId = lastSlot.getDishSlots().get(0).getDishId();
        lastSlot.setPickedDishId(lastPickedId);
        List<Long> sqlFilter = new ArrayList<>();
        sqlFilter.add(firstPickedId);
        sqlFilter.add(lastPickedId);
        testRequest.setProposal(proposalEntity);
        ProposalSlotEntity fillInProposalSlot = proposalSlotHash.get(0);
        int origCount = fillInProposalSlot.getDishSlots().size();


        // set context
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(0, target, proposalEntity);
        testRequest.setContext(context);
        int origApproach = context.getCurrentApproachIndex();

        // set fill in tag slot - not a picked one
        testRequest.setFillInSlotNumber(0);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, sqlFilter);
            Assert.assertNotNull(rawSearchResults.toString());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), eq(sqlFilter)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 1 slots with 9 dishes
        boolean noFilteredDishMatch = true;
        Assert.assertEquals(1, testResult.getResultSlots().size());
        ProposalSlotEntity slot = testResult.getResultSlots().get(0);
        // at least one tag match
        boolean tagMatch = false;
        int dishSlotCount = origCount + 5;
        Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
        for (DishSlotEntity dishSlot : slot.getDishSlots()) {
            tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
            if (tagMatch) {
                break;
            }
            if (sqlFilter.contains(dishSlot.getDishId())) {
                noFilteredDishMatch = false;
                break;
            }
        }
        Assert.assertTrue(tagMatch);


        // currentApproach is 1
        Assert.assertEquals(origApproach, testResult.getCurrentApproach());
        Assert.assertTrue(noFilteredDishMatch);
    }

    @Test
    public void testFillInProposal_NoPicked() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setTarget(target);


        // proposal without picked dishes
        testRequest.setProposal(proposalEntity);

        // set context
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(0, target, proposalEntity);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(0);

        // original context index
        int originalApproach = context.getCurrentApproachIndex();

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, new ArrayList<Long>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 1 slots with 10 dishes
        boolean noFilteredDishMatch = true;
        Assert.assertEquals(1, testResult.getResultSlots().size());
        ProposalSlotEntity slot = testResult.getResultSlots().get(0);
        // at least one tag match
        boolean tagMatch = false;
        int dishSlotCount = 9;
        Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
        for (DishSlotEntity dishSlot : slot.getDishSlots()) {
            tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
            if (tagMatch) {
                break;
            }
        }
        Assert.assertTrue(tagMatch);


        // currentApproach is 1
        Assert.assertEquals(originalApproach, testResult.getCurrentApproach());
    }

    @Test
    public void testFillInProposal_MealPlanFillPicked() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(6, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setTarget(target);


        // get dummy meal plan
        MealPlanEntity mealPlan = ProcessorTestUtils.getDummyMealPlan(3);
        List<Long> sqlFilter = new ArrayList<>();

        testRequest.setMealPlan(mealPlan);

        // set picked dish in first and last slots
        Map<Integer, ProposalSlotEntity> proposalSlotHash = new HashMap<>();
        proposalEntity.getSlots().forEach(t -> proposalSlotHash.put(t.getSlotNumber(), t));
        ProposalSlotEntity firstSlot = proposalSlotHash.get(0);
        Long firstPickedId = firstSlot.getDishSlots().get(0).getDishId();
        firstSlot.setPickedDishId(firstPickedId);
        ProposalSlotEntity lastSlot = proposalSlotHash.get(3);
        Long lastPickedId = lastSlot.getDishSlots().get(0).getDishId();
        lastSlot.setPickedDishId(lastPickedId);
        sqlFilter.add(firstPickedId);
        sqlFilter.add(lastPickedId);
        testRequest.setProposal(proposalEntity);

        // add meal plan ids to sql filter
        mealPlan.getSlots().forEach(s -> sqlFilter.add(s.getDish().getId()));

        // set context
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(0, target, proposalEntity);
        context.setCurrentApproachIndex(3);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(0);
        int origDishCount = proposalSlotHash.get(0).getDishSlots().size();

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    35, 3, true, false, sqlFilter);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), eq(sqlFilter)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 8 slots with 5 dishes each
        Assert.assertEquals(1, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            int dishSlotCount = origDishCount + 5;
            Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 1
        Assert.assertEquals(3, testResult.getCurrentApproach());
    }

    @Test
    public void testFillInProposal_MealPlanNoPickedFill() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setTarget(target);


        // get dummy meal plan
        MealPlanEntity mealPlan = ProcessorTestUtils.getDummyMealPlan(3);
        List<Long> sqlFilter = new ArrayList<>();

        testRequest.setMealPlan(mealPlan);

        // set proposal
        testRequest.setProposal(proposalEntity);

        // add meal plan ids to sql filter
        mealPlan.getSlots().forEach(s -> sqlFilter.add(s.getDish().getId()));

        // set context
        int contextIndex = 0;
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(contextIndex, target, proposalEntity);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(0);


        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, sqlFilter);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), eq(sqlFilter)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(1, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            int dishSlotCount = 9;
            Assert.assertEquals(dishSlotCount, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 1
        Assert.assertEquals(contextIndex, testResult.getCurrentApproach());
    }


    @Test
    public void testFillInProposal_NoTargetTagIds() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4, 2, 3);
        target.setTargetTagIds("");
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // set proposal
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setProposal(proposalEntity);

        // set context
        int contextIndex = 1;
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(contextIndex, target, proposalEntity);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(1);

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, new ArrayList<>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(1, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            Assert.assertEquals(9, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(contextIndex, testResult.getCurrentApproach());

    }

    @Test
    public void testFillInProposal_NoSlotsTargetIds() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(2, 2, 3);
        TargetSlotEntity targetSlot = target.getSlots().get(0);
        targetSlot.setTargetTagIds(null);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);


        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setProposal(proposalEntity);

        // set context
        int contextIndex = 1;
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(contextIndex, target, proposalEntity);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(1);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, new ArrayList<>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(1, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            Assert.assertEquals(9, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(contextIndex, testResult.getCurrentApproach());

    }

    @Test
    public void testFillInProposal_OneSlotOnly() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(1, 2, 3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class), eq(TestConstants.USER_3_ID)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // set proposal
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
        testRequest.setProposal(proposalEntity);

        // set context
        int contextApproach = 1;
        ProposalContextEntity context = ProcessorTestUtils.getDummyContext(contextApproach, target, proposalEntity);
        testRequest.setContext(context);
        testRequest.setFillInSlotNumber(0);


        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false, new ArrayList<>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                            any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(1, testResult.getResultSlots().size());
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            Assert.assertEquals(9, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(contextApproach, testResult.getCurrentApproach());
    }
}
