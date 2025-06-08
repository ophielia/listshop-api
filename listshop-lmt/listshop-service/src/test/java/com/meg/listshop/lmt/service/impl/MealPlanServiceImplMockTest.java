/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionIgnoredException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.data.entity.SlotEntity;
import com.meg.listshop.lmt.data.repository.MealPlanRepository;
import com.meg.listshop.lmt.data.repository.SlotRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.lmt.service.proposal.ProposalService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class MealPlanServiceImplMockTest {


    private MealPlanService mealPlanService;


    @MockBean
    private UserService userService;
    @MockBean
    private MealPlanRepository mealPlanRepository;
    @MockBean
    private SlotRepository slotRepository;
    @MockBean
    private DishService dishService;
    @MockBean
    private ProposalService targetProposalService;
    @MockBean
    private TagService tagService;


    private UserEntity userAccount;
    private UserEntity deleteUserAccount;
    private UserEntity modifyUserAccount;


    @Before
    public void setUp() {
        mealPlanService = new MealPlanServiceImpl(userService,
                mealPlanRepository,
                slotRepository,
                dishService,
                targetProposalService,
                tagService);


        userAccount = createTestUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL);
        deleteUserAccount = createTestUser(TestConstants.USER_3_ID,
                TestConstants.USER_3_NAME);
        modifyUserAccount = createTestUser(TestConstants.USER_2_ID,
                TestConstants.USER_2_NAME);
    }


    @Test
    public void createMealPlan_AssignedName() throws Exception {
        String testMealPlanName = "testname";
        Long currentdate = new Date().getTime();
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setName(testMealPlanName);
        ArgumentCaptor<MealPlanEntity> mealPlanEntityCaptor = ArgumentCaptor.forClass(MealPlanEntity.class);

        Mockito.when(userService.getUserByUserEmail(modifyUserAccount.getEmail())).thenReturn(modifyUserAccount);
        Mockito.when(mealPlanRepository.save(mealPlanEntityCaptor.capture())).thenReturn(new MealPlanEntity());

        // Test call
        mealPlanService.createMealPlan(modifyUserAccount.getEmail(), testSave);


        Mockito.verify(userService).getUserByUserEmail(modifyUserAccount.getEmail());
        // assert name, userid, and created correct in object sent to save
        Assert.assertNotNull(mealPlanEntityCaptor.getValue());
        MealPlanEntity savedMealPlan = mealPlanEntityCaptor.getValue();
        Assert.assertEquals(testMealPlanName, savedMealPlan.getName());
        Assert.assertEquals(modifyUserAccount.getId(), savedMealPlan.getUserId());
        Assert.assertTrue(savedMealPlan.getCreated().getTime() >= currentdate);


    }

    @Test
    public void createMealPlan_EmptyName() throws Exception {
        String testMealPlanName = "testname";
        Long currentdate = new Date().getTime();
        MealPlanEntity testSave = new MealPlanEntity();
        ArgumentCaptor<MealPlanEntity> mealPlanEntityCaptor = ArgumentCaptor.forClass(MealPlanEntity.class);

        Mockito.when(userService.getUserByUserEmail(modifyUserAccount.getEmail())).thenReturn(modifyUserAccount);
        Mockito.when(mealPlanRepository.save(mealPlanEntityCaptor.capture())).thenReturn(new MealPlanEntity());

        // Test call
        mealPlanService.createMealPlan(modifyUserAccount.getEmail(), testSave);


        Mockito.verify(userService).getUserByUserEmail(modifyUserAccount.getEmail());
        // assert name, userid, and created correct in object sent to save
        Assert.assertNotNull(mealPlanEntityCaptor.getValue());
        MealPlanEntity savedMealPlan = mealPlanEntityCaptor.getValue();
        Assert.assertNotNull(savedMealPlan.getName());
        Assert.assertEquals(modifyUserAccount.getId(), savedMealPlan.getUserId());
        Assert.assertTrue(savedMealPlan.getCreated().getTime() >= currentdate);


    }

    @Test
    public void testDeleteMealPlan() {
        String email = deleteUserAccount.getEmail();
        Long mealPlanId = 99L;
        MealPlanEntity mealPlan = new MealPlanEntity();
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        List<SlotEntity> slotList = Collections.singletonList(slot);
        mealPlan.setId(mealPlanId);
        mealPlan.setUserId(deleteUserAccount.getId());
        mealPlan.setSlots(Collections.singletonList(slot));
        Mockito.when(userService.getUserByUserEmail(email)).thenReturn(deleteUserAccount);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlan));
        ArgumentCaptor<MealPlanEntity> mealPlanEntityCaptor = ArgumentCaptor.forClass(MealPlanEntity.class);

        mealPlanService.deleteMealPlan(email, mealPlanId);

        Mockito.verify(slotRepository).deleteAll(slotList);
        Mockito.verify(mealPlanRepository).delete(mealPlanEntityCaptor.capture());

        Assert.assertNotNull(mealPlanEntityCaptor.getValue());
        Assert.assertTrue(CollectionUtils.isEmpty(mealPlanEntityCaptor.getValue().getSlots()));

    }

    @Test
    public void testAddDishToMealPlan() {
        String username = "mrtest";
        Long mealPlanId = 9L;
        Long userid = 3L;
        Long dishId = 5L;

        UserEntity user = new UserEntity();
        user.setId(userid);
        user.setEmail(username);
        user.setUsername(username);

        MealPlanEntity mealPlan = new MealPlanEntity();
        mealPlan.setName("The AUSTRIAN");
        mealPlan.setUserId(userid);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);

        SlotEntity slot1 = createSlotWithDish(mealPlan, 11111L);
        SlotEntity slot2 = createSlotWithDish(mealPlan, 111112L);
        List<SlotEntity> slotList = new ArrayList<>();
        slotList.add(slot1);
        slotList.add(slot2);

        Mockito.when(userService.getUserByUserEmail(username)).thenReturn(user);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlan));
        Mockito.when(dishService.getDishForUserById(username, dishId)).thenReturn(dish);
        Mockito.when(slotRepository.findByMealPlanAndDish(mealPlan, dish)).thenReturn(Collections.emptyList());
        Mockito.when(slotRepository.findByMealPlan(mealPlan)).thenReturn(slotList);
        ArgumentCaptor<MealPlanEntity> mealPlanCapture = ArgumentCaptor.forClass(MealPlanEntity.class);
        Mockito.when(mealPlanRepository.save(mealPlanCapture.capture())).thenReturn(null);

        mealPlanService.addDishToMealPlan(username, mealPlanId, dishId);

        // Assertions
        MealPlanEntity result = mealPlanCapture.getValue();
        Assert.assertNotNull("Meal plan should have been saved", result);
        Assert.assertNotNull("Meal plan should have slots", result.getSlots());
        Assert.assertEquals("Meal plan should have 3 slots", 3, result.getSlots().size());
        // create map of dishes in slots
        Map<Long, DishEntity> mealPlanDishIds = result.getSlots().stream()
                .map(SlotEntity::getDish)
                .collect(Collectors.toMap(DishEntity::getId, Function.identity()));
        Assert.assertTrue("Meal plan should contain newly added dish", mealPlanDishIds.containsKey(dishId));
    }


    @Test(expected = ActionIgnoredException.class)
    public void testAddDishToMealPlan_DishExistsKO() {
        String username = "mrtest";
        Long mealPlanId = 9L;
        Long userid = 3L;
        Long dishId = 5L;

        UserEntity user = new UserEntity();
        user.setId(userid);
        user.setEmail(username);
        user.setUsername(username);

        MealPlanEntity mealPlan = new MealPlanEntity();
        mealPlan.setName("The AUSTRIAN");
        mealPlan.setUserId(userid);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);

        SlotEntity slot1 = createSlotWithDish(mealPlan, 11111L);
        SlotEntity slot2 = createSlotWithDish(mealPlan, 111112L);
        List<SlotEntity> slotList = new ArrayList<>();
        slotList.add(slot1);
        slotList.add(slot2);

        Mockito.when(userService.getUserByUserEmail(username)).thenReturn(user);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlan));
        Mockito.when(dishService.getDishForUserById(username, dishId)).thenReturn(dish);
        Mockito.when(slotRepository.findByMealPlanAndDish(mealPlan, dish)).thenReturn(Collections.singletonList(slot1));
        Mockito.when(slotRepository.findByMealPlan(mealPlan)).thenReturn(slotList);
        ArgumentCaptor<MealPlanEntity> mealPlanCapture = ArgumentCaptor.forClass(MealPlanEntity.class);
        Mockito.when(mealPlanRepository.save(mealPlanCapture.capture())).thenReturn(null);

        mealPlanService.addDishToMealPlan(username, mealPlanId, dishId);

        // No Assertions - should have thrown exception
    }

    @Test
    public void testGetRatingsForMealPlan() {
        String userName = userAccount.getEmail();
        Long mealPlanId = 99L;
        MealPlanEntity mealPlan = new MealPlanEntity();
        SlotEntity slot1 = createSlotWithDish(mealPlan, 99L);
        SlotEntity slot2 = createSlotWithDish(mealPlan, 100L);
        SlotEntity slot3 = createSlotWithDish(mealPlan, 101L);
        List<SlotEntity> slotList = Arrays.asList(slot1, slot2, slot3);
        mealPlan.setId(mealPlanId);
        mealPlan.setUserId(userAccount.getId());
        mealPlan.setSlots(slotList);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(userAccount);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlan));

        mealPlanService.getRatingsForMealPlan(userName, mealPlanId);

        Mockito.verify(tagService).getRatingUpdateInfoForDishIds(
                slotList.stream()
                        .map(s -> s.getDish().getId())
                        .collect(Collectors.toList()));

    }

    @Test
    public void testCopyMealPlan() {
        String username = TestConstants.USER_2_NAME;
        Long mealPlanId = 901L;
        Long copiedMealPlanId = 9901L;
        Long userid = 9L;
        Long dishId1 = 11L;
        Long dishId2 = 22L;

        UserEntity user = new UserEntity();
        user.setId(userid);
        user.setEmail(username);
        user.setUsername(username);

        MealPlanEntity sourceMealPlan = new MealPlanEntity();
        sourceMealPlan.setName("The AUSTRIAN");
        sourceMealPlan.setUserId(userid);
        SlotEntity slot1 = createSlotWithDish(sourceMealPlan, dishId1);
        SlotEntity slot2 = createSlotWithDish(sourceMealPlan, dishId2);
        sourceMealPlan.setSlots(Arrays.asList(slot1, slot2));

        MealPlanEntity createdMealPlan = new MealPlanEntity();
        createdMealPlan.setId(copiedMealPlanId);
        createdMealPlan.setUserId(userid);

        Mockito.when(userService.getUserByUserEmail(username)).thenReturn(user);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(sourceMealPlan));
        ArgumentCaptor<MealPlanEntity> savedMealPlan = ArgumentCaptor.forClass(MealPlanEntity.class);
        Mockito.when(mealPlanRepository.save(savedMealPlan.capture())).thenReturn(createdMealPlan);

        mealPlanService.copyMealPlan(username, mealPlanId);

        // check argument sent to save - should have recent date, and same dishes as source
        List<MealPlanEntity> results = savedMealPlan.getAllValues();
        Assert.assertNotNull(results);
        MealPlanEntity nameResult = results.get(0);
        MealPlanEntity result = results.get(1);
        Assert.assertNotNull(result);
        Assert.assertNotEquals("names should NOT match", sourceMealPlan.getName(), nameResult.getName());
        Assert.assertNotNull("name should be filled in", nameResult.getName());
        Assert.assertNotNull("should have slots", result.getSlots());
        Assert.assertEquals("should have two slots", 2, result.getSlots().size());
        List<SlotEntity> copiedSlots = result.getSlots();
        for (SlotEntity slot : copiedSlots) {
            Assert.assertNotNull(slot);
        }
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testCopyMealPlan_NoMealPlanKO() {
        String username = TestConstants.USER_2_NAME;
        Long mealPlanId = 901L;
        Long copiedMealPlanId = 9901L;
        Long userid = 9L;
        Long dishId1 = 11L;
        Long dishId2 = 22L;

        UserEntity user = new UserEntity();
        user.setId(userid);
        user.setEmail(username);
        user.setUsername(username);

        MealPlanEntity sourceMealPlan = new MealPlanEntity();
        sourceMealPlan.setName("The AUSTRIAN");
        sourceMealPlan.setUserId(userid);
        SlotEntity slot1 = createSlotWithDish(sourceMealPlan, dishId1);
        SlotEntity slot2 = createSlotWithDish(sourceMealPlan, dishId2);
        sourceMealPlan.setSlots(Arrays.asList(slot1, slot2));

        MealPlanEntity createdMealPlan = new MealPlanEntity();
        createdMealPlan.setId(copiedMealPlanId);
        createdMealPlan.setUserId(userid);

        Mockito.when(userService.getUserByUserEmail(username)).thenReturn(user);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.empty());
        ArgumentCaptor<MealPlanEntity> savedMealPlan = ArgumentCaptor.forClass(MealPlanEntity.class);
        Mockito.when(mealPlanRepository.save(savedMealPlan.capture())).thenReturn(createdMealPlan);

        mealPlanService.copyMealPlan(username, mealPlanId);

    }

    @Test(expected = ObjectNotYoursException.class)
    public void testCopyMealPlan_NotOwnerKO() {
        String username = TestConstants.USER_2_NAME;
        Long mealPlanId = 901L;
        Long copiedMealPlanId = 9901L;
        Long userid = 9L;
        Long dishId1 = 11L;
        Long dishId2 = 22L;

        UserEntity user = new UserEntity();
        user.setId(userid);
        user.setEmail(username);
        user.setUsername(username);

        MealPlanEntity sourceMealPlan = new MealPlanEntity();
        sourceMealPlan.setName("The AUSTRIAN");
        sourceMealPlan.setUserId(201L);
        SlotEntity slot1 = createSlotWithDish(sourceMealPlan, dishId1);
        SlotEntity slot2 = createSlotWithDish(sourceMealPlan, dishId2);
        sourceMealPlan.setSlots(Arrays.asList(slot1, slot2));

        MealPlanEntity createdMealPlan = new MealPlanEntity();
        createdMealPlan.setId(copiedMealPlanId);
        createdMealPlan.setUserId(userid);

        Mockito.when(userService.getUserByUserEmail(username)).thenReturn(user);
        Mockito.when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(sourceMealPlan));
        ArgumentCaptor<MealPlanEntity> savedMealPlan = ArgumentCaptor.forClass(MealPlanEntity.class);
        Mockito.when(mealPlanRepository.save(savedMealPlan.capture())).thenReturn(createdMealPlan);

        mealPlanService.copyMealPlan(username, mealPlanId);

    }

    private SlotEntity createSlotWithDish(MealPlanEntity mealPlan, Long dishId) {
        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        return slot;
    }

    private UserEntity createTestUser(Long userId, String userName) {
        UserEntity testUser = new UserEntity(userName, userName);
        testUser.setEmail(userName + "@test.com");
        testUser.setId(userId);
        return testUser;
    }

    public void testCreateMealPlanFromProposal() {
    }

    public void testRenameMealPlan() {
    }


}
