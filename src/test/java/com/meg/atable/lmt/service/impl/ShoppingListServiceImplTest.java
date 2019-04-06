package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.ItemRepository;
import com.meg.atable.lmt.data.repository.MealPlanRepository;
import com.meg.atable.lmt.data.repository.ShoppingListRepository;
import com.meg.atable.lmt.service.ShoppingListException;
import com.meg.atable.lmt.service.ShoppingListProperties;
import com.meg.atable.lmt.service.ShoppingListService;
import com.meg.atable.lmt.service.tag.TagService;
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
import java.util.Optional;

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
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ShoppingListProperties shoppingListProperties;
    private UserEntity userAccount;  // user_id 500
    private UserEntity addUserAccount;  // user_id 501
    private TagEntity tag1; // 500
    private TagEntity cheddarTag; // 18

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_NAME);
        addUserAccount = userService.getUserByUserEmail(TestConstants.USER_2_NAME);
        // make tags
        tag1 = tagService.getTagById(TestConstants.TAG_1_ID);
        cheddarTag = tagService.getTagById(18L); // 18 is cheddar tag id;
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(TestConstants.USER_1_NAME);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 2);
    }

    @Test
    public void testGetListByUsername() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getEmail(), TestConstants.LIST_1_ID);


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
    public void testCreateList() throws ShoppingListException {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setRawListType("PickUpList");


        ShoppingListEntity result = shoppingListService.createList(addUserAccount.getEmail(), properties);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreatedOn());
        Assert.assertEquals(ListType.PickUpList, result.getListType());
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void testDeleteList() {
        boolean result = shoppingListService.deleteList(userAccount.getEmail(), TestConstants.LIST_3_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAddItemToList() {
        // make item (unsaved)
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setTagId(tag1.getId());

        // add to baseList
        shoppingListService.addItemToList(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID, itemEntity);

        // retrieve baselist
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // ensure item is there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size() > 0);

        boolean newTagExists = false;
        for (ItemEntity item : result.getItems()) {
            if (item.getTag().getId().equals(tag1.getId())) {
                newTagExists = true;
            }
        }
        Assert.assertTrue(newTagExists);

        // add existing item
        // get initial item count
        int initialCount = result.getItems().size();
        itemEntity.setTagId(cheddarTag.getId());
        shoppingListService.addItemToList(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID, itemEntity);

        // retrieve baselist
        result = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);
        Assert.assertEquals(initialCount, result.getItems().size());

        for (ItemEntity item : result.getItems()) {
            if (item.getTag().getId().equals(cheddarTag.getId())) {
                Assert.assertEquals(2L, item.getUsedCount().longValue());
            }
        }
    }

    @Test
    //@FlywayTest(locationsForMigrate = "classpath:db/testdata/shoppingListServiceImpl_deleteItem")
    //@FlywayTest(invokeBaselineDB = true)
    public void testDeleteItemFromList() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getEmail(), TestConstants.LIST_1_ID);
        int sizeBefore = result.getItems().size();

        // delete from active list
        shoppingListService.deleteItemFromList(userAccount.getEmail(), TestConstants.LIST_1_ID, TestConstants.ITEM_1_ID, false, null);

        // retrieve active list
        result = shoppingListService.getListById(userAccount.getEmail(), TestConstants.LIST_1_ID);

        // ensure item is NOT there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(sizeBefore - 1, result.getItems().size());
    }

    @Test
    public void testGenerateListFromMealPlan() {
        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getEmail(), TestConstants.MEAL_PLAN_1_ID);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProperties() {
        Assert.assertNotNull(shoppingListProperties);
        Assert.assertNotNull(shoppingListProperties.getTestValue());
        Assert.assertEquals("beep", shoppingListProperties.getTestValue());
        Assert.assertNotNull(shoppingListProperties.getDefaultLayouts());
        Assert.assertEquals(5, shoppingListProperties.getDefaultLayouts().entrySet().size());
    }

    @Test
    public void testCategorizeList() {
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);

        List<Category> categoryEntities = shoppingListService.categorizeList(userAccount.getEmail(), result, null, false, null);
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

    @Test
    public void addDishToList() throws ShoppingListException {
        // use test data list which contains onions
        final Long LIST_ID = TestConstants.LIST_2_ID;
        final Long ONION_TAG_ID = 16L;
        final Long HAMBURGER_TAG_ID = 435L;
        final Long DISH_ID = 16L;
        final String USER_NAME = TestConstants.USER_3_NAME;

        // add dish cheeseburger maccoroni  // dish_id 16
        this.shoppingListService.addDishToList(USER_NAME, LIST_ID, DISH_ID);

        // get list
        ShoppingListEntity list = this.shoppingListService.getListById(USER_NAME, LIST_ID);

        boolean hasHamburger = false, hasOnion = false;
        for (ItemEntity item : list.getItems()) {
            if (item.getTag().getId().equals(HAMBURGER_TAG_ID)) {
                hasHamburger = true;
                Assert.assertTrue(item.getRawDishSources().contains(String.valueOf(DISH_ID)));
            } else if (item.getTag().getId().equals(ONION_TAG_ID)) {
                hasOnion = true;
                Assert.assertEquals(2L, item.getUsedCount().longValue());
                Assert.assertTrue(item.getRawDishSources().contains(String.valueOf(DISH_ID)));
            }
        }
        // MM TODO - test for sources, when sources are complete
        Assert.assertTrue(hasHamburger);
        Assert.assertTrue(hasOnion);
    }

    @Test
    public void removeDishFromList() throws Exception {
        // use list 2 - modification
        // list 2 has dish kate salad tuna, chickpeas and scallion
        ShoppingListEntity list = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // get total count of items (including usedcount)
        Integer startSum = list.getItems().stream()
                .mapToInt(t -> t.getUsedCount()).sum();

        // call remove dish
        shoppingListService.removeDishFromList(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID, 83L);

        // retrieve list
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // get total count of items (including usedcount)
        Integer resultSum = result.getItems().stream()
                .mapToInt(t -> t.getUsedCount()).sum();

        // should be 3 items less
        Assert.assertTrue(startSum - resultSum.longValue() <= 4);

        // go through all ensuring
        //  no tuna (210)
        //  no chickpeas (113)
        //  no scallion (211)
        // and no dish_source for kate salad (83)
        for (ItemEntity item : result.getItems()) {
            if (item.getTag().getId().equals(210L)) {
                Assert.fail("tuna found");
            }
            if (item.getTag().getId().equals(113L)) {
                if (item.getUsedCount() > 1)
                    Assert.fail("chickpeas found, used more than once");
            }
            if (item.getTag().getId().equals(211L)) {
                Assert.fail("scallion found");
            }
            if (item.getRawDishSources() != null && item.getRawDishSources().contains(";83;")) {
                Assert.fail("dish id still found in source");
            }
        }

    }

    @Test
    public void fillSources() throws Exception {
        // test begin state - list contains
        //    broccoli(21) for scoozi (90)
        //    onions(16) for cheeseburger maccaroni (16)
        //    honey (359) for pickup list
        //    cat food (470) for base list

        // get listEntity (list 2)
        ShoppingListEntity shoppingListEntity = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // call fillSources
        shoppingListService.fillSources(shoppingListEntity);

        // results -
        // dish sources should be 2 - scoozi (90) and cheeseburger maccaroni (16)
        Assert.assertTrue(shoppingListEntity.getDishSources().size() >= 2);
        boolean hasScoozi = false;
        boolean hasCheeseMac = false;
        for (DishEntity dish : shoppingListEntity.getDishSources()) {
            if (dish.getId().equals(90L)) {
                hasScoozi = true;
            } else if (dish.getId().equals(16L)) {
                hasCheeseMac = true;
            }
        }
        Assert.assertTrue(hasScoozi);
        Assert.assertTrue(hasCheeseMac);
        Optional<DishEntity> test = shoppingListEntity.getDishSources().stream().filter(d -> d.getId().equals(16L)).findFirst();
        Assert.assertTrue(test.isPresent()); // cheeseburger maccaroni there
        test = shoppingListEntity.getDishSources().stream().filter(d -> d.getId().equals(90L)).findFirst();
        Assert.assertTrue(test.isPresent()); // scoozi there
        // list sources should be 2 - pickuplist and baselist
        Assert.assertEquals(2, shoppingListEntity.getListSources().size());
        Optional<String> testListSource = shoppingListEntity.getListSources().stream().filter(d -> d.equals(ItemSourceType.BaseList.name())).findFirst();
        Assert.assertTrue(testListSource.isPresent()); // base list there
        testListSource = shoppingListEntity.getListSources().stream().filter(d -> d.equals(ItemSourceType.PickUpList.name())).findFirst();
        Assert.assertTrue(testListSource.isPresent()); // pickup list there
    }

    @Test
    public void changeListLayout() throws Exception {
        // use modified list - which begins with layout fine grained (1)
        // and will be changed to all

        ShoppingListEntity modifiedList = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);
        Long origLayout = modifiedList.getListLayoutId();

        shoppingListService.changeListLayout(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID, TestConstants.LIST_LAYOUT_3_ID);

        ShoppingListEntity check = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);
        Assert.assertNotEquals(origLayout, check.getListLayoutId());
    }

    @Test
    public void testHighlightDish() throws ShoppingListException {

        shoppingListService.addDishToList(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID, 110L);
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        List<Category> categoryEntities = shoppingListService.categorizeList(TestConstants.USER_3_NAME, result, 110L, false, null);
        Assert.assertNotNull(categoryEntities);

        // should find category with category id -1
        boolean highlightCategoryFound = false;
        for (Category categoryResult : categoryEntities) {
            ItemCategory cr = (ItemCategory) categoryResult;
            if (CategoryType.Highlight.name().equals(cr.getCategoryType())) {
                highlightCategoryFound = true;
                break;
            }
        }
        Assert.assertTrue(highlightCategoryFound);
    }
}