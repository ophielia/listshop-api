package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.MealPlanType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.MealPlanRepository;
import com.meg.atable.data.repository.SlotRepository;
import com.meg.atable.service.MealPlanService;
import io.jsonwebtoken.lang.Collections;
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

    @Autowired
    private SlotRepository slotRepository;

    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;
    private static MealPlanEntity retrieve;
    private static String noseyUserName;

    @Autowired
    private DishRepository dishRepository;

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
        String userName = "mealPlanTest";
        userAccount = userService.save(new UserAccountEntity(userName, "password"));

        noseyUserName = "noseyUser";
        userService.save(new UserAccountEntity(noseyUserName, "password"));

        retrieve = buildMealPlan("for retrieve", userAccount.getId());
        retrieve = mealPlanRepository.save(retrieve);

        MealPlanEntity listPlan = buildMealPlan("list plan", userAccount.getId());
        mealPlanRepository.save(listPlan);

        SlotEntity slot1 = buildDishSlot(retrieve, "testDish1");
        SlotEntity slot2 = buildDishSlot(retrieve, "testDish2");
        List<SlotEntity> slots = Collections.arrayToList(new SlotEntity[]{slot1, slot2});
        slotRepository.save(slots);

        retrieve.setSlots(slots);
        mealPlanRepository.save(retrieve);

        setUpComplete = true;

    }

    @Test
    public void createMealPlan() throws Exception {
        MealPlanEntity testSave = new MealPlanEntity();
        testSave.setName("testname");
        testSave.setCreated(new Date());
        testSave.setMealPlanType(MealPlanType.Manual);

        testSave = mealPlanService.createMealPlan(userAccount.getUsername(), testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(userAccount.getUsername(), id);
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(), check.getName());
        Assert.assertEquals(testSave.getCreated(), check.getCreated());
        Assert.assertEquals(testSave.getMealPlanType(), check.getMealPlanType());
    }


    @Test
    public void getMealPlanById() throws Exception {
        // get id for retrieve - already set up
        Long id = retrieve.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(userAccount.getUsername(), id);

        Assert.assertNotNull(check);
        Assert.assertEquals(retrieve.getId(), check.getId());
        Assert.assertEquals(retrieve.getCreated(), check.getCreated());
        Assert.assertEquals(retrieve.getUserId(), check.getUserId());
        Assert.assertEquals(retrieve.getName(), check.getName());
    }

    @Test
    public void getMealPlanById_BadUser() throws Exception {
        // get id for retrieve - already set up
        Long id = retrieve.getId();

        MealPlanEntity check = mealPlanService.getMealPlanById(noseyUserName, id);

        Assert.assertNull(check);
    }

    @Test
    public void getMealPlanList() throws Exception {
        List<MealPlanEntity> list = mealPlanService.getMealPlansForUserName(userAccount.getUsername());

        Assert.assertNotNull(list);
        Assert.assertEquals(2L, list.size());
    }

    @Test
    public void testAddDishToMealPlan() throws Exception {
        DishEntity dish = new DishEntity(userAccount.getId(), "added slot");
        dish = dishRepository.save(dish);

        mealPlanService.addDishToMealPlan(userAccount.getUsername(), retrieve.getId(), dish.getId());

        MealPlanEntity testMealPlan = mealPlanService
                .getMealPlanById(userAccount.getUsername(), retrieve.getId());
        List<SlotEntity> testSlots = testMealPlan.getSlots();
        Integer newSlotCount = testSlots.size();

        Assert.assertTrue(1 == newSlotCount);
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
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(userAccount.getUsername(), retrieve.getId());
        List<SlotEntity> slots = mealPlan.getSlots();
        Long toBeRemoved = slots.get(0).getDish().getId();
        int origSize = slots.size();

        mealPlanService.deleteDishFromMealPlan(userAccount.getUsername(), retrieve.getId(), toBeRemoved);

        MealPlanEntity result = mealPlanService.getMealPlanById(userAccount.getUsername(), retrieve.getId());
        Assert.assertNotNull(result);
        if (origSize == 1) {
            Assert.assertNull(result.getSlots());
        } else {
            Assert.assertTrue(result.getSlots().size() == origSize - 1);
        }
    }

    @Test
    public void testDeleteMealPlan() {
        MealPlanEntity mealPlan = mealPlanService.getMealPlanById(userAccount.getUsername(), retrieve.getId());

        boolean success = mealPlanService.deleteMealPlan(userAccount.getUsername(), mealPlan.getId());
        MealPlanEntity testPlan = mealPlanService.getMealPlanById(userAccount.getUsername(), retrieve.getId());
        Assert.assertNull(testPlan);
        Assert.assertTrue(success);
    }

    private SlotEntity buildDishSlot(MealPlanEntity mealplan, String testDish1) {
        DishEntity dish = new DishEntity();
        dish.setDishName(testDish1);
        dish = this.dishRepository.save(dish);
        SlotEntity slot = new SlotEntity();
        slot.setDish(dish);
        slot.setMealPlan(mealplan);
        return slot;
    }

    private MealPlanEntity buildMealPlan(String mealPlanName, Long userId) {
        MealPlanEntity testMealPlan = new MealPlanEntity();
        testMealPlan.setCreated(new Date());
        testMealPlan.setName(mealPlanName);
        testMealPlan.setUserId(userId);
        return testMealPlan;
    }


}