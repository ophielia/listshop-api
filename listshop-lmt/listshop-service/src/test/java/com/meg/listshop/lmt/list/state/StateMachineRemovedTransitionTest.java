package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ServiceTestUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StateMachineRemovedTransitionTest {

    @ClassRule
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
    public void testRemoveByTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();

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
        verifyRemovedAndUpdated(result);


    }

    @Test
    public void testRemoveByDish() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // setup - adding a dish item from a tag
        TagEntity tagEntity = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tagEntity);
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

        // we expect that the result has been logically removed
        // (since the last detail was removed)
        verifyRemovedAndUpdated(result);

        // we expect that there aren't any detail items
        Assertions.assertEquals(0, countDetailItems(testItem));

    }

    @Test
    public void testRemoveByDishTwoItems() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // setup - adding two dish item from a tag
        TagEntity tagEntity = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setDishItem(dishItem);
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        Long secondDishId = 999999L;
        DishItemEntity secondDishItem = createDishItem(secondDishId,tagEntity);
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
    public void testRemoveByList() throws ItemProcessingException {
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

        ListItemEntity testResult = listItemRepository.findWithDetailsById(result.getId()).orElse(null);
        // we expect that the result has been logically removed
        // (since the last detail was removed)
        verifyRemovedAndUpdated(testResult);

        // we expect that there aren't any detail items
        Assertions.assertEquals(0, countDetailItems(testResult));

    }

    private ListItemDetailEntity createDetailItem(ListItemEntity item) {
            ListItemDetailEntity detail = new ListItemDetailEntity();
            detail.setItem(item);
            return itemDetailRepository.save(detail);
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

    private void verifyRemovedAndUpdated(ListItemEntity result) {
        Assertions.assertNotNull(result.getRemovedOn());
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getRemovedOn(),2));
        Assertions.assertNotNull(result.getUpdatedOn());
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(),2));
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
