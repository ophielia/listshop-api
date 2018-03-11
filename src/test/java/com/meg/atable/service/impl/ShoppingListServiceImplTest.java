package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.Category;
import com.meg.atable.api.model.ItemCategory;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.*;
import com.meg.atable.service.ShoppingListProperties;
import com.meg.atable.service.ShoppingListService;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
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
    @Autowired
    private ShoppingListProperties shoppingListProperties;
    private UserAccountEntity userAccount;  // user_id 500
    private UserAccountEntity addUserAccount;  // user_id 501
    private TagEntity tag1; // 500
    private ItemEntity itemEntity; // 500
    private ShoppingListEntity baseList;  // 500
    private ShoppingListEntity activeList; // 501
    private ShoppingListEntity toDelete; // 502
    private MealPlanEntity finalMealPlan; // 500

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserName(TestConstants.USER_1_NAME);
        addUserAccount = userService.getUserByUserName(TestConstants.USER_2_NAME);
        // make tags
        tag1 = tagService.getTagById(TestConstants.TAG_1_ID).get();

        // make base list
        //baseList = shoppingListService.getListById(LIST_1_ID);

        // make active list
        activeList = shoppingListRepository.getOne(TestConstants.LIST_2_ID);
        itemEntity = itemRepository.getOne(TestConstants.ITEM_1_ID);

        // make list to be deleted
        toDelete = shoppingListRepository.getOne(TestConstants.LIST_3_ID);

        // make a mealplan with three dishes, and five tags
        finalMealPlan = mealPlanRepository.getOne(TestConstants.MEAL_PLAN_1_ID);
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(userAccount.getUsername());

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);
    }

    @Test
    public void testGetListByUsername() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), TestConstants.LIST_1_ID);


        Assert.assertNotNull(result);
        Assert.assertEquals(ListType.BaseList, result.getListType());
        Assert.assertEquals(TestConstants.LIST_1_ID, result.getId());
    }


    @Test
    public void testGetListByUsername_BadUser() {
        ShoppingListEntity result = shoppingListService.getListById("noseyusername",
                TestConstants.LIST_1_ID);

        Assert.assertNull(result);
    }

    @Test
    public void testCreateList() {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setListType(ListType.BaseList);
        shoppingListEntity.setListLayoutType(ListLayoutType.All);

        ShoppingListEntity result = shoppingListService.createList(addUserAccount.getUsername(), shoppingListEntity);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreatedOn());
        Assert.assertNotNull(result.getListLayoutType());
        Assert.assertEquals(shoppingListEntity.getListType(), result.getListType());
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void testDeleteList() {
        boolean result = shoppingListService.deleteList(userAccount.getUsername(), TestConstants.LIST_3_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAddItemToList() {
        // make item (unsaved)
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setTag(tag1);

        // add to baseList
        shoppingListService.addItemToList(userAccount.getUsername(), TestConstants.LIST_1_ID, itemEntity);

        // retrieve baselist
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), TestConstants.LIST_1_ID);

        // ensure item is there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size() > 0);
        Assert.assertNotNull(result.getItems().get(0).getId());
    }

    @Test
    //@FlywayTest(locationsForMigrate = "classpath:db/testdata/shoppingListServiceImpl_deleteItem")
    //@FlywayTest(invokeBaselineDB = true)
    public void testDeleteItemFromList() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), TestConstants.LIST_1_ID);
        int sizeBefore = result.getItems().size();

        // delete from active list
        shoppingListService.deleteItemFromList(userAccount.getUsername(), TestConstants.LIST_1_ID, TestConstants.ITEM_1_ID);

        // retrieve active list
        result = shoppingListService.getListById(userAccount.getUsername(), TestConstants.LIST_1_ID);

        // ensure item is NOT there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(sizeBefore - 1, result.getItems().size());
    }

    @Test
    public void testGenerateListFromMealPlan() {
        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getUsername(), TestConstants.MEAL_PLAN_1_ID);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProperties() {
        Assert.assertNotNull(shoppingListProperties);
        Assert.assertNotNull(shoppingListProperties.getTestValue());
        Assert.assertEquals("beep", shoppingListProperties.getTestValue());
        Assert.assertNotNull(shoppingListProperties.getDefaultLayouts());
        Assert.assertEquals(4, shoppingListProperties.getDefaultLayouts().entrySet().size());
    }

    @Test
    public void testCategorizeList() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), TestConstants.MEAL_PLAN_1_ID);

        List<Category> categoryEntities = shoppingListService.categorizeList(result);
        Assert.assertNotNull(categoryEntities);

        // count items and subcategories
        int itemcount = 0;
        int subcatcount = 0;
        for (Category categoryResult : categoryEntities) {
            ItemCategory cr = (ItemCategory) categoryResult;
            itemcount += cr.getItemEntities().size();
            if (!categoryResult.getSubCategories().isEmpty()) {
                subcatcount += categoryResult.getSubCategories().size();
                itemcount += categoryResult.getSubCategories()
                        .stream()
                        .mapToInt(sc -> ((ItemCategory) sc).getItemEntities().size())
                        .sum();
            }

        }
        Assert.assertEquals(5, itemcount);
        Assert.assertEquals(2, subcatcount);
    }

}