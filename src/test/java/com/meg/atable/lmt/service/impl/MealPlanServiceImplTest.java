package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.MealPlanEntity;
import com.meg.atable.lmt.data.entity.SlotEntity;
import com.meg.atable.lmt.data.repository.DishRepository;
import com.meg.atable.lmt.data.repository.MealPlanRepository;
import com.meg.atable.lmt.service.MealPlanService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MealPlanServiceImplTest {
    @Autowired
    private MealPlanService mealPlanService;


    @Autowired
    private UserService userService;

    @Autowired
    private MealPlanRepository mealPlanRepository;


    private  UserAccountEntity userAccount;
    private  UserAccountEntity deleteUserAccount;
    private  UserAccountEntity modifyUserAccount;

    @Autowired
    private DishRepository dishRepository;

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserName(TestConstants.USER_1_NAME);
        deleteUserAccount = userService.getUserByUserName(TestConstants.USER_3_NAME);
        modifyUserAccount = userService.getUserByUserName(TestConstants.USER_2_NAME);

    }

    @Test
    public void createMealPlan() throws Exception {
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setName("testname");
        testSave.setCreated(new Date());

        testSave = mealPlanService.createMealPlan(modifyUserAccount.getUsername(), testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(modifyUserAccount.getUsername(), id);
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(), check.getName());
        Assert.assertEquals(testSave.getCreated(), check.getCreated());
        Assert.assertEquals(testSave.getMealPlanType(), check.getMealPlanType());
    }


    @Test
    public void createMealPlan_EmptyName() throws Exception {
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setCreated(new Date());

        testSave = mealPlanService.createMealPlan(modifyUserAccount.getUsername(), testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(modifyUserAccount.getUsername(), id);
        Assert.assertNotNull(check);
        Assert.assertNotNull(check.getName());
        Assert.assertEquals(testSave.getCreated(), check.getCreated());
        Assert.assertEquals(testSave.getMealPlanType(), check.getMealPlanType());
    }

    @Test
    public void getMealPlanById() throws Exception {
        // get id for retrieve - already set up
        MealPlanEntity check = mealPlanService.getMealPlanById(userAccount.getUsername(), TestConstants.MEAL_PLAN_1_ID);

        Assert.assertNotNull(check);
        Assert.assertEquals(500L, check.getId().longValue());
        Long timeSinceCreated = (new Date().getTime() -  check.getCreated().getTime())/1000;
        Assert.assertTrue(timeSinceCreated < 15);// created 10 seconds ago
        Assert.assertEquals(userAccount.getId(), check.getUserId());
        Assert.assertEquals(TestConstants.MENU_PLAN_1_NAME, check.getName());
    }

    @Test(expected = ObjectNotYoursException.class)
    public void getMealPlanById_BadUser() throws Exception {
        // get id for retrieve - already set up
        Long id = TestConstants.MEAL_PLAN_1_ID;

        MealPlanEntity check = mealPlanService.getMealPlanById(deleteUserAccount.getUsername(), id);

        Assert.assertNull(check);
    }

    @Test
    public void getMealPlanList() throws Exception {
        List<MealPlanEntity> list = mealPlanService.getMealPlansForUserName(userAccount.getUsername());

        Assert.assertNotNull(list);
        Assert.assertEquals(1L, list.size());
    }

    @Test
    public void testAddDishToMealPlan() throws Exception {
        DishEntity dish = new DishEntity(modifyUserAccount.getId(), "added slot");
        dish = dishRepository.save(dish);

        MealPlanEntity beginMealPlan = mealPlanService
                .getMealPlanById(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID);
        List<SlotEntity> beginSlots = beginMealPlan.getSlots();
        Integer beginSlotCount = beginSlots.size();

        mealPlanService.addDishToMealPlan(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID, dish.getId());

        MealPlanEntity testMealPlan = mealPlanService
                .getMealPlanById(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID);
        List<SlotEntity> testSlots = testMealPlan.getSlots();
        Integer newSlotCount = testSlots.size();

        Assert.assertEquals(1,newSlotCount - beginSlotCount);
        boolean dishfound = false;
        for (SlotEntity slot : testSlots) {
            if (slot.getDish().getDishName().equals("added slot")) {
                dishfound = true;
            }
        }
        Assert.assertTrue(dishfound);
    }


    @Test
    public void testDeleteDishFromMealPlan() {
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID);
        List<SlotEntity> slots = mealPlan.getSlots();
        Long toBeRemoved = slots.get(0).getDish().getId();
        int origSize = slots.size();

        mealPlanService.deleteDishFromMealPlan(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID, toBeRemoved);

        MealPlanEntity result = mealPlanService.getMealPlanById(modifyUserAccount.getUsername(), TestConstants.MENU_PLAN_2_ID);
        Assert.assertNotNull(result);
        if (origSize == 1) {
            Assert.assertNull(result.getSlots());
        } else {
            Assert.assertTrue(result.getSlots().size() == origSize - 1);
        }
    }

    @Test(expected=ObjectNotFoundException.class)
    public void testDeleteMealPlan() {
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(deleteUserAccount.getUsername(), TestConstants.MENU_PLAN_4_ID);

        boolean success = mealPlanService.deleteMealPlan(deleteUserAccount.getUsername(), mealPlan.getId());
        MealPlanEntity testPlan = mealPlanService.getMealPlanById(deleteUserAccount.getUsername(), TestConstants.MENU_PLAN_4_ID);
        Assert.assertNull(testPlan);
        Assert.assertTrue(success);
    }

    public void testCreateMealPlanFromProposal() {}
    public void testRenameMealPlan () {}

}