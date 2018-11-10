package com.meg.atable.lmt.service.impl;

import com.meg.atable.lmt.api.exception.ProposalProcessingException;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.ProposalContextRepository;
import com.meg.atable.lmt.service.*;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by margaretmartin on 03/06/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@Transactional
public class ProposalGeneratorServiceImplIntTest {


    private static Long existingProposalId;

    @Autowired
    private
    ProposalGeneratorService proposalGeneratorServiceImpl;

    @Autowired
    private ProposalService proposalService;


    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private ProposalContextRepository proposalContextRepository;

    @Autowired
    private TargetService targetService;

    @Autowired
    private DishService dishService;

    private List<Long> testDishIds;


    @Before
    public void setUp() throws ProposalProcessingException {
        // make / save base target
        TargetEntity targetEntity = buildBaseTestTarget();


        /*

        List<ContextApproachEntity> approachEntities = makeDummyContextApproaches(new ProposalContextEntity(), 3, 5);
        ProcessResult processResult = new ProcessResult(approachEntities);
        List<ProposalSlotEntity> resultSlots = makeDummyProposalForTarget(targetEntity);
        processResult.setResultSlots(resultSlots);
        Mockito.when(newSearchProcessor.processProposal(any(ProposalRequest.class))).thenReturn(processResult);
        // run "dummy" mock proposal
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.generateProposal(TestConstants.USER_3_NAME, targetEntity.getTargetId());
        // save result as existingProposalId
        existingProposalId = proposalEntity.getId();
         */
    }

    @Test
    public void testNewSearchGenerateProposal() throws Exception {
        // make / save b    ase target
        TargetEntity targetEntity = buildBaseTestTarget();
        // make test fixtures
        ProposalRequest request = new ProposalRequest();
        request.setSearchType(ProposalSearchType.NewSearch);
        request.setTarget(targetEntity);

        // test call
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.generateProposal(TestConstants.USER_3_NAME, targetEntity.getTargetId());

        // retrieve result
        Assert.assertNotNull(proposalEntity);
        ProposalEntity result = proposalService.getProposalById(TestConstants.USER_3_NAME, proposalEntity.getId());

        // result exists
        Assert.assertNotNull(result);
        // result has same number of slots as target
        Assert.assertEquals(targetEntity.getSlots().size(), result.getSlots().size());
        // slots contain 5 dishes each
        for (ProposalSlotEntity slotEntity : result.getSlots()) {
            Assert.assertTrue(slotEntity.getDishSlots().size() == 5);
        }
        // has approaches and is refreshable
        ProposalContextEntity context = proposalContextRepository.findByProposalId(result.getId());
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getApproaches());
        Assert.assertTrue(context.getApproaches().size()>1);
        Assert.assertEquals(0, context.getCurrentApproachIndex());
    }


    /*


    @Test
    public void refreshProposal() throws Exception {
        ProposalEntity proposal = proposalService.getProposalById(TestConstants.USER_3_NAME, existingProposalId);
        ProposalContextEntity contextentity = proposalContextRepository.findByProposalId(proposal.getId());
        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_3_NAME, contextentity.getTargetId());
        // make test fixtures
        List<ContextApproachEntity> approachEntities = makeDummyContextApproaches(new ProposalContextEntity(), 3, 5);
        ProcessResult processResult = new ProcessResult(approachEntities);
        List<ProposalSlotEntity> resultSlots = makeDummyProposalForTarget(targetEntity);
        processResult.setResultSlots(resultSlots);
        Mockito.when(refreshProcessor.processProposal(any(ProposalRequest.class))).thenReturn(processResult);

        // test call
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.refreshProposal(TestConstants.USER_3_NAME, existingProposalId);

        // retrieve result
        Assert.assertNotNull(proposalEntity);
        ProposalEntity result = proposalService.getProposalById(TestConstants.USER_3_NAME, proposalEntity.getId());

        // result exists
        Assert.assertNotNull(result);
        // result has same number of slots as target
        Assert.assertEquals(targetEntity.getSlots().size(), result.getSlots().size());
        // slots contain 5 dishes each
        for (ProposalSlotEntity slotEntity : result.getSlots()) {
            Assert.assertTrue(slotEntity.getDishSlots().size() == 5);
        }
        // has approaches and is refreshable
        ProposalContextEntity context = proposalContextRepository.findByProposalId(result.getId());
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getApproaches());
        Assert.assertEquals(3, context.getApproaches().size());
        Assert.assertEquals(0, context.getCurrentApproachIndex());
    }


    @Test
    public void proposalForMealPlan() throws Exception {
        // make / save base target
        TargetEntity targetEntity = buildBaseTestTarget();
        // make / save meal plan
        MealPlanEntity mealPlanEntity = buildTestMealPlan(4);
        // make test fixtures
        ProposalRequest request = new ProposalRequest();
        request.setSearchType(ProposalSearchType.NewSearch);
        request.setTarget(targetEntity);
        List<ContextApproachEntity> approachEntities = makeDummyContextApproaches(new ProposalContextEntity(), 3, 5);
        ProcessResult processResult = new ProcessResult(approachEntities);
        List<ProposalSlotEntity> resultSlots = makeDummyProposalForTarget(targetEntity);
        processResult.setResultSlots(resultSlots);
        Mockito.when(newSearchProcessor.processProposal(any(ProposalRequest.class))).thenReturn(processResult);


        // test call
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.proposalForMealPlan(TestConstants.USER_3_NAME, mealPlanEntity.getId(), targetEntity.getTargetId());

        // retrieve result
        Assert.assertNotNull(proposalEntity);
        ProposalEntity result = proposalService.getProposalById(TestConstants.USER_3_NAME, proposalEntity.getId());

        // result exists
        Assert.assertNotNull(result);
        // result has same number of slots as target
        Assert.assertEquals(targetEntity.getSlots().size(), result.getSlots().size());
        // slots contain 5 dishes each
        for (ProposalSlotEntity slotEntity : result.getSlots()) {
            Assert.assertTrue(slotEntity.getDishSlots().size() == 5);
        }
        // has approaches and is refreshable
        ProposalContextEntity context = proposalContextRepository.findByProposalId(result.getId());
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getApproaches());
        Assert.assertEquals(3, context.getApproaches().size());
        Assert.assertEquals(0, context.getCurrentApproachIndex());
    }


    @Test
    public void addToProposalSlot() throws Exception {
        ProposalEntity proposal = proposalService.getProposalById(TestConstants.USER_3_NAME, existingProposalId);
        ProposalContextEntity contextentity = proposalContextRepository.findByProposalId(proposal.getId());
        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_3_NAME, contextentity.getTargetId());
        // make test fixtures
        List<ContextApproachEntity> approachEntities = makeDummyContextApproaches(new ProposalContextEntity(), 3, 5);
        ProcessResult processResult = new ProcessResult(approachEntities);
        List<ProposalSlotEntity> resultSlots = makeDummyProposalForTarget(targetEntity);
        resultSlots = resultSlots.subList(0, 1);
        processResult.setResultSlots(resultSlots);
        Mockito.when(fillInProcessor.processProposal(any(ProposalRequest.class))).thenReturn(processResult);

        // test call
        ProposalEntity proposalEntity = proposalGeneratorServiceImpl.addToProposalSlot(TestConstants.USER_3_NAME, existingProposalId, 0);

// retrieve result
        Assert.assertNotNull(proposalEntity);
        ProposalEntity result = proposalService.getProposalById(TestConstants.USER_3_NAME, existingProposalId);

        // result exists
        Assert.assertNotNull(result);
        // result has same number of slots as target
        Assert.assertEquals(targetEntity.getSlots().size(), result.getSlots().size());
        // slots contain 5 dishes each
        for (ProposalSlotEntity slotEntity : result.getSlots()) {
            Assert.assertTrue(slotEntity.getDishSlots().size() == 5);
        }
        // has approaches and is refreshable
        ProposalContextEntity context = proposalContextRepository.findByProposalId(result.getId());
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getApproaches());
        Assert.assertEquals(3, context.getApproaches().size());
        Assert.assertEquals(0, context.getCurrentApproachIndex());
    }

    @Test
    public void fillInformationForProposal() throws Exception {
        ProposalEntity proposal = proposalService.getProposalById(TestConstants.USER_3_NAME, existingProposalId);

        proposal = proposalGeneratorServiceImpl.fillInformationForProposal(TestConstants.USER_3_NAME, proposal);

        // make sure everything is filled in
        Assert.assertNotNull(proposal.getTargetTags());
        Assert.assertNotNull(proposal.getSlots());
        for (ProposalSlotEntity slot : proposal.getSlots()) {
            Set<Long> ids = FlatStringUtils.inflateStringToLongSet(slot.getFlatMatchedTagIds(), ";");
            Assert.assertTrue(ids.size() <= slot.getTags().size());
            for (DishSlotEntity dish : slot.getDishSlots()) {
                if (dish.getMatchedTagIds() != null) {
                    Assert.assertTrue(dish.getMatchedTags().size() > 0);
                }
            }
        }

    }

    private List<ContextApproachEntity> makeDummyContextApproaches(ProposalContextEntity proposalContext, int approachCount, int slotCount) {
        List<String> slotNumbers = new ArrayList<>();
        for (int i = 0; i < slotCount; i++) {
            slotNumbers.add(String.valueOf(i));
        }
        List<ContextApproachEntity> approaches = new ArrayList<>();
        for (int i = 0; i < approachCount; i++) {
            ContextApproachEntity entity = new ContextApproachEntity();
            entity.setApproachNumber(i);
            entity.setContext(proposalContext);
            List<String> tempList = slotNumbers.subList(i, slotNumbers.size());
            if (i > 0) {
                tempList.addAll(slotNumbers.subList(0, i - 1));
            }
            entity.setInstructions(FlatStringUtils.flattenListToString(tempList, ";"));
            approaches.add(entity);
        }
        return approaches;
    }

    private List<ProposalSlotEntity> makeDummyProposalForTarget(TargetEntity targetEntity) {
        List<ProposalSlotEntity> dummySlots = new ArrayList<>();


        Set<Long> targetIds = FlatStringUtils.inflateStringToLongSet(targetEntity.getTargetTagIds(), ";");
        for (TargetSlotEntity slot : targetEntity.getSlots()) {
            ProposalSlotEntity proposalSlot = makeDummySlot(5, slot, targetIds);
            dummySlots.add(proposalSlot);
        }

        return dummySlots;
    }

    private ProposalSlotEntity makeDummySlot(int dishCount, TargetSlotEntity slot, Set<Long> targetIds) {
        List<Long> testDishIds = getTestDishIds();
        List<String> tagIdsForSlot = slot.getTagIdsAsList();
        tagIdsForSlot.addAll(targetIds.stream().map(String::valueOf).collect(Collectors.toList()));
        ProposalSlotEntity proposalSlot = new ProposalSlotEntity();
        int slotNumber = slot.getSlotOrder();
        proposalSlot.setSlotNumber(slotNumber);
        int offset = slotNumber * dishCount;
        int tagCounter = 0;
        List<DishSlotEntity> dishSlots = new ArrayList<>();
        for (int dishCounter = offset; dishCounter < offset + 5; dishCounter++) {
            DishSlotEntity dishSlot = new DishSlotEntity();
            dishSlot.setDishId(testDishIds.get(dishCounter));
            if (tagCounter < tagIdsForSlot.size()) {
                dishSlot.setMatchedTagIds(tagIdsForSlot.get(tagCounter));
                tagCounter++;
            } else {
                String matchedTags = FlatStringUtils.flattenListToString(tagIdsForSlot, ";");
                dishSlot.setMatchedTagIds(matchedTags);
                tagCounter = 0;
            }
            dishSlot.setSlot(proposalSlot);
            dishSlots.add(dishSlot);
        }
        proposalSlot.setDishSlots(dishSlots);
        return proposalSlot;
    }

    private List<Long> getTestDishIds() {
        if (testDishIds != null) {
            return testDishIds;
        }
        testDishIds = new ArrayList<>();
        List<DishEntity> dishes = dishService.getDishesForUserName(TestConstants.USER_3_NAME);
        int upperLimit = Math.min(dishes.size(), 100);
        for (int i = 0; i < upperLimit; i++) {
            testDishIds.add(dishes.get(i).getId());
        }
        return testDishIds;
    }

    private MealPlanEntity buildTestMealPlan(int dishCount) {
        Random random = new Random();
        List<Long> testDishIds = getTestDishIds();
        MealPlanEntity mealPlanEntity = mealPlanService.createMealPlan(TestConstants.USER_3_NAME, new MealPlanEntity());

        int maxValue = testDishIds.size();
        int begin = random.nextInt(maxValue);
        for (int i = begin; i < begin + dishCount; i++) {
            int index = i >= maxValue ? i - begin : i;
            Long dishId = testDishIds.get(index);
            mealPlanService.addDishToMealPlan(TestConstants.USER_3_NAME, mealPlanEntity.getId(), dishId);
        }
        return mealPlanEntity;
    }


     */

    private TargetEntity buildBaseTestTarget() {
        TargetEntity target = targetService.createTarget(TestConstants.USER_3_NAME, new TargetEntity());

        TargetSlotEntity slot1 = new TargetSlotEntity();
        TargetSlotEntity slot2 = new TargetSlotEntity();
        TargetSlotEntity slot3 = new TargetSlotEntity();
        TargetSlotEntity slot4 = new TargetSlotEntity();
        slot1.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot2.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        slot3.setSlotDishTagId(TestConstants.TAG_SIDE_DISH);
        slot4.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);


        targetService.addSlotToTarget(TestConstants.USER_3_NAME, target.getTargetId(), slot1);
        targetService.addSlotToTarget(TestConstants.USER_3_NAME, target.getTargetId(), slot2);
        targetService.addSlotToTarget(TestConstants.USER_3_NAME, target.getTargetId(), slot3);
        targetService.addSlotToTarget(TestConstants.USER_3_NAME, target.getTargetId(), slot4);

        // now, add tags
        targetService.addTagToTarget(TestConstants.USER_3_NAME, target.getTargetId(), TestConstants.TAG_YUMMY);
        targetService.addTagToTarget(TestConstants.USER_3_NAME, target.getTargetId(), TestConstants.TAG_CARROTS);

        target = targetService.getTargetById(TestConstants.USER_3_NAME, target.getTargetId());
        TargetSlotEntity slot = target.getSlots().get(0);
        targetService.addTagToTargetSlot(TestConstants.USER_3_NAME, target.getTargetId(), slot.getId(), TestConstants.TAG_CROCKPOT);
        slot = target.getSlots().get(1);
        targetService.addTagToTargetSlot(TestConstants.USER_3_NAME, target.getTargetId(), slot.getId(), TestConstants.TAG_SOUP);
        slot = target.getSlots().get(2);
        targetService.addTagToTargetSlot(TestConstants.USER_3_NAME, target.getTargetId(), slot.getId(), TestConstants.TAG_MEAT);
        slot = target.getSlots().get(3);
        targetService.addTagToTargetSlot(TestConstants.USER_3_NAME, target.getTargetId(), slot.getId(), TestConstants.TAG_PASTA);


        return targetService.getTargetById(TestConstants.USER_3_NAME, target.getTargetId());

    }

}