package com.meg.atable.service.impl;

import com.meg.atable.Application;
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
import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ShoppingListServiceImplTest {
    private final static String USER_1_NAME = "testuser";
    private final static String USER_2_NAME = "adduser";
    private static final Long MEAL_PLAN_1_ID = 500L;
    private static final Long ITEM_1_ID = 500L;
    private static final Long TAG_1_ID = 500L;
    private static final Long LIST_1_ID = 500L;
    private static final Long LIST_2_ID = 501L;
    private static final Long LIST_3_ID = 502L;
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
        userAccount = userService.getUserByUserName(USER_1_NAME);
        addUserAccount = userService.getUserByUserName(USER_2_NAME);
        // make tags
        tag1 = tagService.getTagById(TAG_1_ID).get();

        // make base list
        //baseList = shoppingListService.getListById(LIST_1_ID);

        // make active list
        activeList = shoppingListRepository.getOne(LIST_2_ID);
        itemEntity = itemRepository.getOne(ITEM_1_ID);

        // make list to be deleted
        toDelete = shoppingListRepository.getOne(LIST_3_ID);

        // make a mealplan with three dishes, and five tags
        finalMealPlan = mealPlanRepository.getOne(MEAL_PLAN_1_ID);
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(userAccount.getUsername());

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);
    }

    @Test
    public void testGetListByUsername() {
    ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),LIST_1_ID);


        Assert.assertNotNull(result);
        Assert.assertEquals(ListType.BaseList, result.getListType());
        Assert.assertEquals(LIST_1_ID,result.getId());
    }


    @Test
    public void testGetListByUsername_BadUser() {
        ShoppingListEntity result = shoppingListService.getListById("noseyusername",
                LIST_1_ID);

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
        boolean result = shoppingListService.deleteList(userAccount.getUsername(), LIST_3_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAddItemToList() {
        // make item (unsaved)
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setListCategory("All");
        itemEntity.setTag(tag1);

        // add to baseList
        shoppingListService.addItemToList(userAccount.getUsername(), LIST_1_ID, itemEntity);

        // retrieve baselist
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), LIST_1_ID);

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
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(), LIST_1_ID);
int sizeBefore = result.getItems().size();

        // delete from active list
        shoppingListService.deleteItemFromList(userAccount.getUsername(), LIST_1_ID, ITEM_1_ID);

        // retrieve active list
         result = shoppingListService.getListById(userAccount.getUsername(), LIST_1_ID);

        // ensure item is NOT there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(sizeBefore - 1,result.getItems().size());
    }

    @Test
    public void testGenerateListFromMealPlan() {
        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getUsername(), MEAL_PLAN_1_ID);
        Assert.assertNotNull(result);
    }
}