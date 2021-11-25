package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TargetType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishTagSearchResult;
import com.meg.listshop.lmt.service.proposal.ProcessResult;
import com.meg.listshop.lmt.service.proposal.ProposalRequest;
import com.meg.listshop.lmt.service.proposal.impl.SearchProposalProcessorImpl;
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
public class SearchProposalProcessorImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private SearchProposalProcessorImpl processor;

    @MockBean
    private TagStructureService tagStructureService;

    @MockBean
    private DishSearchService dishSearchService;



    @Test
    @Ignore
    public void processProposal_NoExistingProposal() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4,2,3);
        target.setTargetType(TargetType.Standard);
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
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
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
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4,2,3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
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
        boolean noFilteredDishMatch = true;
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
                if (sqlFilter.contains(dishSlot.getDishId())) {
                    noFilteredDishMatch = false;
                    break;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(0, testResult.getCurrentApproach());
        // resultApproach > 0   - random results with 2 slots lead to valid one result only sometimes.
        Assert.assertTrue(testResult.getResultApproaches().size() > 0);
        Assert.assertTrue(noFilteredDishMatch);
    }

    @Test
    public void processProposal_MealPlanExistingProposal() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4,2,3);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        ProposalEntity proposalEntity = ProcessorTestUtils.getProposalForTarget(target);
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

        // get dummy meal plan
        MealPlanEntity mealPlan = ProcessorTestUtils.getDummyMealPlan(3);
        mealPlan.getSlots().forEach(s -> sqlFilter.add(s.getDish().getId()));
        testRequest.setMealPlan(mealPlan);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
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

    @Test
    public void processProposal_MealPlanNoExistingProposal() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4,2,3);
        target.setTargetType(TargetType.Standard);
        target.setUserId(TestConstants.USER_3_ID);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);

        // get dummy meal plan
        MealPlanEntity mealPlan = ProcessorTestUtils.getDummyMealPlan(3);
        List<Long> sqlFilter = new ArrayList<>();
        mealPlan.getSlots().forEach(s -> sqlFilter.add(s.getDish().getId()));
        testRequest.setMealPlan(mealPlan);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false,sqlFilter);
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                    any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(4, testResult.getResultSlots().size());
        boolean noFilteredDishMatch = true;
        for (ProposalSlotEntity slot : testResult.getResultSlots()) {
            // at least one tag match
            boolean tagMatch = false;
            Assert.assertEquals(5, slot.getDishSlots().size());
            for (DishSlotEntity dishSlot : slot.getDishSlots()) {
                tagMatch = !StringUtils.isEmpty(dishSlot.getMatchedTagIds());
                if (tagMatch) {
                    break;
                }
                if (sqlFilter.contains(dishSlot.getDishId())) {
                    noFilteredDishMatch = false;
                }
            }
            Assert.assertTrue(tagMatch);
        }

        // currentApproach is 0
        Assert.assertEquals(0, testResult.getCurrentApproach());
        // resultApproach > 1
        Assert.assertTrue(testResult.getResultApproaches().size() > 1);
        Assert.assertTrue(noFilteredDishMatch);
    }

    @Test
    public void processProposal_NoTargetTagIds() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(4,2,3);
        target.setTargetTagIds("");
        target.setTargetType(TargetType.Standard);
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
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
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
    public void processProposal_NoSlotsTargetIds() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(2,2,3);
        target.setTargetType(TargetType.Standard);
        TargetSlotEntity targetSlot = target.getSlots().get(0);
        targetSlot.setTargetTagIds(null);
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
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false,new ArrayList<>());
            Mockito.when(dishSearchService.retrieveDishResultsForTags(eq(TestConstants.USER_3_ID), eq(slot), any(Integer.class),
                    any(List.class), any(Map.class), any(List.class)))
                    .thenReturn(rawSearchResults);
        }

        ProcessResult testResult = processor.processProposal(testRequest);

        Assert.assertNotNull(testResult);
        // check 4 slots with 5 dishes each
        Assert.assertEquals(2, testResult.getResultSlots().size());
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

    }

    @Test
    public void processProposal_OneSlotOnly() throws Exception {
        TargetEntity target = ProcessorTestUtils.getDummyTarget(1,2,3);
        target.setUserId(TestConstants.USER_3_ID);
        target.setTargetType(TargetType.Standard);
        ProposalRequest testRequest = new ProposalRequest();
        testRequest.setTarget(target);

        // get tag structure dummy results
        Mockito.when(tagStructureService.getSearchGroupsForTagIds(any(Set.class)))
                .thenReturn(new HashMap<Long, List<Long>>());

        // get raw results
        Map<String, Boolean> dishTagMatches = new HashMap<String, Boolean>();
        for (TargetSlotEntity slot : target.getSlots()) {
            // get List<DishTagSearchResult> for slot
            List<DishTagSearchResult> rawSearchResults = ProcessorTestUtils.makeDummySearchResults(slot,
                    dishTagMatches, target.getTagIdsAsSet(), target.getTagIdsAsSet().size(),
                    20, 3, true, false,new ArrayList<>());
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
    }
}