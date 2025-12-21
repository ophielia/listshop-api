package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
class StateMachineRemovedTransitionTest {

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

    @Test
    void testRemoveByTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        // remove by tag physically deletes the tag
        Long listId = targetList.getId();
        // setup - adding a simple list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // test context
        ItemStateContext testContext = new ItemStateContext(testItem, listId);
        testContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext);

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
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // count detail items - we expect 1
        Assertions.assertEquals(1, countDetailItems(testItem));
        // test context
        ItemStateContext testContext = new ItemStateContext(testItem, listId);
        testContext.setDishId(dishId);
        // call to test
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext);

        // we expect that the result has been physically removed
        // (since the last detail was removed)
        Assertions.assertNull(result);

        // we expect that there aren't any detail items
        Assertions.assertEquals(0, countDetailItems(testItem));

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
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        Long secondDishId = 999999L;
        DishItemEntity secondDishItem = createDishItem(secondDishId, tagEntity);
        setupContext = new ItemStateContext(testItem, listId);
        setupContext.setDishItem(secondDishItem);
        testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // count detail items - we expect 2
        Assertions.assertEquals(2, countDetailItems(testItem));
        // test context
        ItemStateContext testContext = new ItemStateContext(testItem, listId);
        testContext.setDishId(dishId);
        // call to test
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext);

        // we expect that the result has been logically removed
        // (since the last detail was removed)
        verifyUpdatedOnly(result);

        // should be 1 detail item remaining
        Assertions.assertEquals(1, countDetailItems(testItem));

    }

    private int countDetailItems(ListItemEntity testItem) {
        if (testItem == null || testItem.getDetails() == null || testItem.getDetails().isEmpty()) {
            return 0;
        }
        return testItem.getDetails().size();
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
        ListItemEntity fromSecondList = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, secondListContext);

        // add item from second list to test target list
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setListItem(fromSecondList);
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // count detail items - we expect 1
        Assertions.assertEquals(1, countDetailItems(testItem));

        // now, the test - remove the second list item from the test target list
        ItemStateContext testContext = new ItemStateContext(testItem, listId);
        testContext.setListItem(fromSecondList);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.REMOVE_ITEM, testContext);

        // we expect that the result has been physically removed
        // (since the last detail was removed)
        Assertions.assertNull(result);

    }


    private ShoppingListEntity createShoppingList() {

        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setName(LocalDateTime.now().toString());
        return shoppingListRepository.save(shoppingListEntity);
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

    private void dateInLastSecond(Date toCheck) {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        LocalDateTime timeToCheck = LocalDateTime.ofInstant(toCheck.toInstant(), ZoneId.systemDefault());
        System.out.println(oneSecondAgo);
        System.out.println(timeToCheck);
        Assertions.assertTrue(timeToCheck.isAfter(oneSecondAgo));

    }
}
