package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.data.entity.SlotEntity;
import com.meg.listshop.lmt.data.repository.MealPlanRepository;
import com.meg.listshop.lmt.data.repository.SlotRepository;
import com.meg.listshop.lmt.service.DishService;
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
                TestConstants.USER_1_NAME);
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

    private UserEntity createTestUser(Long userId, String userName) {
        UserEntity testUser = new UserEntity(userName, userName);
        testUser.setEmail(userName + "@test.com");
        testUser.setId(userId);
        return testUser;
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

        Mockito.verify(tagService).getRatingUpdateInfoForDishIds(userName,
                slotList.stream()
                        .map(s -> s.getDish().getId())
                        .collect(Collectors.toList()));

    }

    private SlotEntity createSlotWithDish(MealPlanEntity mealPlan, Long dishId) {
        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        return slot;
    }


    public void testCreateMealPlanFromProposal() {
    }

    public void testRenameMealPlan() {
    }
}