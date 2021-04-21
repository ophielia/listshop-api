package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingInfo;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.data.entity.SlotEntity;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MealPlanServiceImplTest {
    @Autowired
    private MealPlanService mealPlanService;


    @Autowired
    private UserService userService;


    private UserEntity userAccount;
    private UserEntity deleteUserAccount;
    private UserEntity modifyUserAccount;

    @Autowired
    private DishRepository dishRepository;

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_NAME);
        deleteUserAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        modifyUserAccount = userService.getUserByUserEmail(TestConstants.USER_2_NAME);

    }

    @Test
    public void createMealPlan() throws Exception {
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setName("testname");
        testSave.setCreated(new Date());

        testSave = mealPlanService.createMealPlan(modifyUserAccount.getEmail(), testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(modifyUserAccount.getEmail(), id);
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(), check.getName());
        Assert.assertEquals(testSave.getCreated(), check.getCreated());
        Assert.assertEquals(testSave.getMealPlanType(), check.getMealPlanType());
    }


    @Test
    public void createMealPlan_EmptyName() throws Exception {
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setCreated(new Date());

        testSave = mealPlanService.createMealPlan(modifyUserAccount.getEmail(), testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(modifyUserAccount.getEmail(), id);
        Assert.assertNotNull(check);
        Assert.assertNotNull(check.getName());
        Assert.assertEquals(testSave.getCreated(), check.getCreated());
        Assert.assertEquals(testSave.getMealPlanType(), check.getMealPlanType());
    }

    @Test
    public void getMealPlanById() throws Exception {
        // get id for retrieve - already set up
        MealPlanEntity check = mealPlanService.getMealPlanById(userAccount.getEmail(), TestConstants.MEAL_PLAN_1_ID);

        Assert.assertNotNull(check);
        Assert.assertEquals(500L, check.getId().longValue());
        Long timeSinceCreated = (new Date().getTime() -  check.getCreated().getTime())/1000;
        Assert.assertEquals(userAccount.getId(), check.getUserId());
        Assert.assertEquals(TestConstants.MENU_PLAN_1_NAME, check.getName());
    }

    @Test(expected = ObjectNotYoursException.class)
    public void getMealPlanById_BadUser() throws Exception {
        // get id for retrieve - already set up
        Long id = TestConstants.MEAL_PLAN_1_ID;

        MealPlanEntity check = mealPlanService.getMealPlanById(deleteUserAccount.getEmail(), id);

        Assert.assertNull(check);
    }

    @Test
    public void getMealPlanList() throws Exception {
        List<MealPlanEntity> list = mealPlanService.getMealPlansForUserName(userAccount.getEmail());

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() >= 2);
    }

    @Test
    public void testAddDishToMealPlan() throws Exception {
        DishEntity dish = new DishEntity(userAccount.getId(), "added slot");
        dish = dishRepository.save(dish);


        MealPlanEntity beginMealPlan = mealPlanService
                .getMealPlanById(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID);
        List<SlotEntity> beginSlots = beginMealPlan.getSlots();
        Integer beginSlotCount = beginSlots.size();

        mealPlanService.addDishToMealPlan(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID, dish.getId());

        MealPlanEntity testMealPlan = mealPlanService
                .getMealPlanById(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID);
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
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID);
        List<SlotEntity> slots = mealPlan.getSlots();
        Long toBeRemoved = slots.get(0).getDish().getId();
        int origSize = slots.size();

        mealPlanService.deleteDishFromMealPlan(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID, toBeRemoved);

        MealPlanEntity result = mealPlanService.getMealPlanById(userAccount.getEmail(), TestConstants.MENU_PLAN_2_ID);
        Assert.assertNotNull(result);
        if (origSize == 1) {
            Assert.assertNull(result.getSlots());
        } else {
            Assert.assertTrue(result.getSlots().size() == origSize - 1);
        }
    }

    @Test(expected=ObjectNotFoundException.class)
    public void testDeleteMealPlan() {
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(deleteUserAccount.getEmail(), TestConstants.MENU_PLAN_4_ID);

        mealPlanService.deleteMealPlan(deleteUserAccount.getEmail(), mealPlan.getId());
        MealPlanEntity testPlan = mealPlanService.getMealPlanById(deleteUserAccount.getEmail(), TestConstants.MENU_PLAN_4_ID);
        Assert.assertNull(testPlan);
    }

    @Test
    public void testGetRatingsForMealPlan() {
        // using meal plan 5 (505) as test , with 2 dishes
        // user id is 500 (user 1)
        String username = TestConstants.USER_1_NAME;
        Long mealPlanId = TestConstants.MENU_PLAN_5_ID;
        RatingUpdateInfo updateInfo = mealPlanService.getRatingsForMealPlan(username, mealPlanId);
        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(8, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(2, updateInfo.getDishRatingInfoSet().size());
        for (Iterator<DishRatingInfo> iter = updateInfo.getDishRatingInfoSet().iterator(); iter.hasNext(); ) {
            DishRatingInfo info = iter.next();
            Assert.assertNotNull(info.getRatings());
            Assert.assertEquals(8, info.getRatings().size());
        }

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        Iterator it = testHeaders.iterator();
        while (it.hasNext()) {
            RatingInfo toTest = (RatingInfo) it.next();
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }

    public void testCreateMealPlanFromProposal() {}
    public void testRenameMealPlan () {}

}