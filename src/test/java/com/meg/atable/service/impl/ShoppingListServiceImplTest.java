package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.*;
import com.meg.atable.service.MealPlanService;
import com.meg.atable.service.ShoppingListService;
import com.meg.atable.service.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ShoppingListServiceImplTest {
    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private TagService tagService;

    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;
    private static TagEntity tag1;
    private static ItemEntity itemEntity;
    private static ShoppingListEntity baseList;
    private static ShoppingListEntity activeList;
    private static ShoppingListEntity toDelete;
    private static String noseyUserName;
    private static MealPlanEntity finalMealPlan;

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
// make user
        String userName = "shoppingListTest";
        userAccount = userService.save(new UserAccountEntity(userName, "password"));
        noseyUserName = "noseyUser";

        // make tags
        tag1 = new TagEntity("tag1", "main1");
        TagEntity tag21 = new TagEntity("tag1", "main1");
        TagEntity tag31 = new TagEntity("tag1", "main1");

        tag1 = tagService.save(tag1);
        tag21 = tagService.save(tag21);
        tag31 = tagService.save(tag31);

        // make base list
        baseList = new ShoppingListEntity();
        baseList.setListType(ListType.BaseList);
        baseList.setCreatedOn(new Date());
        baseList.setUserId(userAccount.getId());
        baseList = shoppingListRepository.save(baseList);

        // make active list
        activeList = new ShoppingListEntity();
        activeList.setListType(ListType.ActiveList);
        activeList.setCreatedOn(new Date());
        activeList.setUserId(userAccount.getId());
        activeList = shoppingListRepository.save(activeList);
        itemEntity = new ItemEntity();
        itemEntity.setListCategory("All");
        itemEntity.setTag(tag1);
        itemEntity.setListId(activeList.getId());
        itemEntity = itemRepository.save(itemEntity);

        // make list to be deleted
        toDelete = new ShoppingListEntity();
        toDelete.setListType(ListType.ActiveList);
        toDelete.setCreatedOn(new Date());
        toDelete.setUserId(userAccount.getId());
        toDelete = shoppingListRepository.save(toDelete);

        // make a mealplan with three dishes, and five tags
        TagEntity tag1 = ServiceTestUtils.buildTag("tag1", TagType.TagType);
        TagEntity tag2 = ServiceTestUtils.buildTag("tag2", TagType.TagType);
        TagEntity tag3 = ServiceTestUtils.buildTag("tag3", TagType.TagType);
        TagEntity tag4 = ServiceTestUtils.buildTag("tag4", TagType.TagType);
        TagEntity tag5 = ServiceTestUtils.buildTag("tag5", TagType.TagType);
        List<TagEntity> tags = Arrays.asList(tag1,tag2,tag3,tag4,tag5);
        List<TagEntity> savedTags =tagRepository.save(tags);

        DishEntity dish1 = ServiceTestUtils.buildDish(userAccount.getId(),"dish1",savedTags.subList(0,2));
        DishEntity dish2 = ServiceTestUtils.buildDish(userAccount.getId(),"dish2",savedTags.subList(2,3));
        DishEntity dish3 = ServiceTestUtils.buildDish(userAccount.getId(),"dish3",savedTags.subList(3,5));
        List<DishEntity> dishes = Arrays.asList(dish1,dish2,dish3);
        List<DishEntity> savedDishes = dishRepository.save(dishes);

        MealPlanEntity mealPlanEntity = ServiceTestUtils.buildMealPlan("testMealPlan",userAccount.getId());
        MealPlanEntity savedMealPlan = mealPlanRepository.save(mealPlanEntity);

        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(savedMealPlan,savedDishes.get(0));
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(savedMealPlan,savedDishes.get(1));
        SlotEntity slot3 = ServiceTestUtils.buildDishSlot(savedMealPlan,savedDishes.get(2));
        List<SlotEntity> slots = Arrays.asList(slot1,slot2,slot3);
        List<SlotEntity> savedSlots = slotRepository.save(slots);

        savedMealPlan.setSlots(savedSlots);
        finalMealPlan = mealPlanRepository.save(savedMealPlan);
        setUpComplete = true;
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(userAccount.getUsername());

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);
    }

    @Test
    public void testGetListByUsername() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),
                baseList.getId());

        Assert.assertNotNull(result);
        Assert.assertEquals(baseList.getCreatedOn(), result.getCreatedOn());
    }


    @Test
    public void testGetListByUsername_BadUser() {
        ShoppingListEntity result = shoppingListService.getListById(noseyUserName,
                baseList.getId());

        Assert.assertNull(result);
    }

    @Test
    public void testCreateList() {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setListType(ListType.BaseList);
        shoppingListEntity.setListLayoutType(ListLayoutType.All);

        ShoppingListEntity result = shoppingListService.createList(userAccount.getUsername(), shoppingListEntity);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreatedOn());
        Assert.assertNotNull(result.getListLayoutType());
        Assert.assertEquals(shoppingListEntity.getListType(), result.getListType());
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void testDeleteList() {
        boolean result = shoppingListService.deleteList(userAccount.getUsername(), toDelete.getId());

        Assert.assertTrue(result);
    }

    @Test
    public void testAddItemToList() {
        // make item (unsaved)
ItemEntity itemEntity = new ItemEntity();
itemEntity.setListCategory("All");
itemEntity.setTag(tag1);

        // add to baseList
        shoppingListService.addItemToList(userAccount.getUsername(),baseList.getId(),itemEntity);

        // retrieve baselist
ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),baseList.getId());

        // ensure item is there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size()>0);
        Assert.assertNotNull(result.getItems().get(0).getId());
    }

    @Test
    public void testDeleteItemFromList() {

        // delete from active list
        shoppingListService.deleteItemFromList(userAccount.getUsername(),activeList.getId(),itemEntity.getId());

        // retrieve active list
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),activeList.getId());

        // ensure item is NOT there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size()==0);
    }

    @Test
    public void testGenerateListFromMealPlan() {
        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getUsername(),finalMealPlan.getId());
        Assert.assertNotNull(result);
    }
}