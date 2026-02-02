package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.api.model.v2.SpecificationType;
import com.meg.listshop.lmt.conversion.BasicAmount;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = {"/com/meg/listshop/lmt/list/state/StateMachineActiveTransitionTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StateMachineRemovedTransitionTest {

    private static final Long GRAM_UNIT_ID = 1013L;
    private static final Long TAG_TOMATO = 33L;
    private static final Long UNIT_UNIT_ID = 1011L;
    private static final Long LB_ID = 1008L;
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @Autowired
    private ListItemStateMachine listItemStateMachine;
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    @Autowired
    private ListItemDetailRepository itemDetailRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ListItemRepository listItemRepository;

    private ListItemEntity createSimpleListItem(TagEntity tagEntity, double quantitv, Long unitId) throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // setup - adding a list item from a tag

        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        if (quantitv > 0) {
            BasicAmount amount = new BasicAmount(quantitv, null, null, unitId, tagEntity);
            setupContext.setTagAmount(amount);
        }
        return listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());
    }

    private ShoppingListEntity createShoppingList() {

        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setName(LocalDateTime.now().toString());
        shoppingListEntity.setUserId(34L);
        return shoppingListRepository.save(shoppingListEntity);
    }

    private int countDetailItems(ListItemEntity testItem) {
        if (testItem == null || testItem.getDetails() == null || testItem.getDetails().isEmpty()) {
            return 0;
        }
        return testItem.getDetails().size();
    }

    private TagEntity createTag() {
        TagEntity tag = new TagEntity();
        tag.setName(LocalDateTime.now().toString());
        tag.setTagType(TagType.Ingredient);
        tag.setInternalStatus(TagInternalStatus.EMPTY);
        return tagRepository.save(tag);
    }

    private DishItemEntity createDishItem(Long dishId, TagEntity tag) {
        DishItemEntity dishItemEntity = new DishItemEntity();
        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(dishId);
        dishItemEntity.setTag(tag);
        dishItemEntity.setDish(dishEntity);
        return dishItemEntity;
    }

    private void verifyUpdatedOnly(ListItemEntity result) {
        Assertions.assertNotNull(result.getUpdatedOn());
        dateInLastSecond(result.getUpdatedOn());
    }

    private TagEntity getTag(Long tagId) {
        return tagRepository.findById(tagId).orElse(null);
    }

    private void dateInLastSecond(Date toCheck) {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        LocalDateTime timeToCheck = LocalDateTime.ofInstant(toCheck.toInstant(), ZoneId.systemDefault());
        System.out.println(oneSecondAgo);
        System.out.println(timeToCheck);
        Assertions.assertTrue(timeToCheck.isAfter(oneSecondAgo));

    }

    @Nested
    class RemovalTestsWithAmounts {
        @Test
        void testRemoveByTag() throws ItemProcessingException {
            ShoppingListEntity targetList = createShoppingList();
            // remove by tag physically deletes the tag
            Long listId = targetList.getId();
            // setup - adding a simple list item from a tag - no relation to other list or dish
            TagEntity tagEntity = createTag();
            BasicAmount amount = new BasicAmount(100.0, null, null, GRAM_UNIT_ID, tagEntity);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setTag(tagEntity);
            setupContext.setTagAmount(amount);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setTag(tagEntity);

            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that the result has the correct dates
            Assertions.assertNull(result);
        }

        @Test
        void testRemoveByDishWithOtherExisting() throws ItemProcessingException {
            // this will remove the dish, but leave the tag
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = createTag();
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            dishItem.setQuantity(100.0);
            dishItem.setUnitId(GRAM_UNIT_ID);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // setup - adding list item
            ListItemEntity listItem = createSimpleListItem(tagEntity, 200.0, GRAM_UNIT_ID);
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setListItem(listItem);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 2
            Assertions.assertEquals(2, countDetailItems(testItem));
            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setDishId(dishId);

            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that 1 detail remains, with total of 200.0 grams
            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, countDetailItems(testItem));
            Assertions.assertEquals(200.0, result.getRawQuantity());
            Assertions.assertEquals(SpecificationType.ALL, result.getSpecificationType());

        }

        @Test
        void testRemoveByListWithOtherExisting() throws ItemProcessingException {
            // this will remove the dish, but leave the tag
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = createTag();
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // setup - adding list item
            ListItemEntity listItem = createSimpleListItem(tagEntity, 200.0, GRAM_UNIT_ID);
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setListItem(listItem);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 2, and mixed specification type
            Assertions.assertEquals(2, countDetailItems(testItem));
            Assertions.assertEquals(SpecificationType.MIXED, testItem.getSpecificationType());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setListItem(listItem);

            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that 1 detail remains, without amount
            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, countDetailItems(testItem));
            Assertions.assertNull(result.getRawQuantity());
            Assertions.assertEquals(SpecificationType.NONE, result.getSpecificationType());

        }

        @Test
        void testRemoveByListWithSum() throws ItemProcessingException {
            // two list items, different list - ensures sum is correct
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = createTag();
            BasicAmount amount = new BasicAmount(150.0, null, null, GRAM_UNIT_ID, tagEntity);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setTag(tagEntity);
            setupContext.setTagAmount(amount);
            ListItemEntity firstItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // setup - adding second list item
            ListItemEntity secondItem = createSimpleListItem(tagEntity, 350.0, GRAM_UNIT_ID);
            setupContext = new ItemStateContext(firstItem, listId);
            setupContext.setListItem(secondItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 2, and ALL specification type
            Assertions.assertEquals(2, countDetailItems(testItem));
            Assertions.assertEquals(SpecificationType.ALL, testItem.getSpecificationType());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setListItem(secondItem);

            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that 1 detail remains, with total of 150.0 grams
            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, countDetailItems(testItem));
            Assertions.assertEquals(150.0, result.getRawQuantity());
            Assertions.assertEquals(SpecificationType.ALL, result.getSpecificationType());

        }

        @Test
        void testRemoveByListThreeTypes() throws ItemProcessingException {
            // item has specified dish, unspecified tag and list
            //          before removal - MIXED.  after removal - NONE
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = createTag();
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            dishItem.setQuantity(100.0);
            dishItem.setUnitId(GRAM_UNIT_ID);

            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // adding list item, unspecified
            ListItemEntity secondListItem = createSimpleListItem(tagEntity, 0, 0L);
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setListItem(secondListItem);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // adding unspecified tag
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setTag(tagEntity);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());


            // count detail items - we expect 3, and MIXED specification type
            Assertions.assertEquals(3, countDetailItems(testItem));
            Assertions.assertEquals(SpecificationType.MIXED, testItem.getSpecificationType());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setDishItem(dishItem);

            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that 2 details remains, without amount
            Assertions.assertNotNull(result);
            Assertions.assertEquals(2, countDetailItems(testItem));
            Assertions.assertNull(result.getRawQuantity());
            Assertions.assertEquals(SpecificationType.NONE, result.getSpecificationType());

        }

        @Test
        void testRemoveByListThreeTypesTagSpecific() throws ItemProcessingException {
            // item has specified dish, unspecified tag and list
            //          before removal - MIXED.  after removal - NONE
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = getTag(TAG_TOMATO);
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            dishItem.setQuantity(1.0);
            dishItem.setUnitId(UNIT_UNIT_ID);

            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // adding list item - 3 tomatoes
            ListItemEntity secondListItem = createSimpleListItem(tagEntity, 3, UNIT_UNIT_ID);
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setListItem(secondListItem);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // adding tag, 1/2 pound
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setTag(tagEntity);
            BasicAmount amount = new BasicAmount(1.0, null, null, LB_ID, tagEntity);
            setupContext.setTagAmount(amount);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());


            // count detail items - we expect 3, and ALL specification type
            Assertions.assertEquals(3, countDetailItems(testItem));
            Assertions.assertEquals(SpecificationType.ALL, testItem.getSpecificationType());
            Assertions.assertEquals(8.0, testItem.getRoundedQuantity());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setDishItem(dishItem);

            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that 2 details remains, without amount
            Assertions.assertNotNull(result);
            Assertions.assertEquals(2, countDetailItems(testItem));
            Assertions.assertEquals(SpecificationType.ALL, testItem.getSpecificationType());
            Assertions.assertEquals(7.0, testItem.getRoundedQuantity());

        }
    }

    @Nested
    class RemovalTestsNoAmounts {
        @Test
        void testRemoveByTag() throws ItemProcessingException {
            ShoppingListEntity targetList = createShoppingList();
            // remove by tag physically deletes the tag
            Long listId = targetList.getId();
            // setup - adding a simple list item from a tag - no relation to other list or dish
            TagEntity tagEntity = createTag();
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setTag(tagEntity);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setTag(tagEntity);

            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that the result has the correct dates
            Assertions.assertNull(result);


        }

        @Test
        void testRemoveByDish() throws ItemProcessingException {
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding a dish item from a tag
            TagEntity tagEntity = createTag();
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 1
            Assertions.assertEquals(1, countDetailItems(testItem));
            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setDishId(dishId);
            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that the result has been physically removed
            // (since the last detail was removed)
            Assertions.assertNull(result);

            // we expect that there aren't any detail items
            Assertions.assertEquals(0, countDetailItems(result));

        }

        @Test
        void testRemoveByDishTwoItems() throws ItemProcessingException {
            ShoppingListEntity targetList = createShoppingList();
            Long listId = targetList.getId();
            // setup - adding two dish item from a tag
            TagEntity tagEntity = createTag();
            Long dishId = 12345L;
            DishItemEntity dishItem = createDishItem(dishId, tagEntity);
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setDishItem(dishItem);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            Long secondDishId = 999999L;
            DishItemEntity secondDishItem = createDishItem(secondDishId, tagEntity);
            setupContext = new ItemStateContext(testItem, listId);
            setupContext.setDishItem(secondDishItem);
            testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 2
            Assertions.assertEquals(2, countDetailItems(testItem));
            // test context
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setDishId(dishId);
            // call to test
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that the result has been logically removed
            // (since the last detail was removed)
            verifyUpdatedOnly(result);

            // should be 1 detail item remaining
            Assertions.assertEquals(1, countDetailItems(testItem));

        }


        @Test
        void testRemoveByList() throws ItemProcessingException {
            ShoppingListEntity targetList = createShoppingList();
            ShoppingListEntity secondList = createShoppingList();
            Long listId = targetList.getId();
            TagEntity tagEntity = createTag();

            // test list with item from second list

            // second list
            ItemStateContext secondListContext = new ItemStateContext(null, secondList.getId());
            secondListContext.setTag(tagEntity);
            ListItemEntity fromSecondList = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, secondListContext, targetList.getUserId());

            // add item from second list to test target list
            ItemStateContext setupContext = new ItemStateContext(null, listId);
            setupContext.setListItem(fromSecondList);
            ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

            // count detail items - we expect 1
            Assertions.assertEquals(1, countDetailItems(testItem));

            // now, the test - remove the second list item from the test target list
            ItemStateContext testContext = new ItemStateContext(testItem, listId);
            testContext.setListItem(fromSecondList);
            ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext, targetList.getUserId());

            // we expect that the result has been physically removed
            // (since the last detail was removed)
            Assertions.assertNull(result);

        }


    }

}
