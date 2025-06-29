package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.common.DateUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StateMachineActiveTransitionTest {

    private static final Long DISH_ID = 5678L;
    private static final Long UNIT_ID = 9101112L;

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ListItemStateMachine listItemStateMachine;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ListItemRepository itemListRepository;

    @Autowired
    private ListItemDetailRepository itemDetailRepository;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ListItemRepository listItemRepository;

    @Test
    public void blowUpTest() throws ConversionPathException, ConversionFactorException {
        Assertions.assertTrue(true);
        Assertions.assertNotNull(listItemStateMachine);
    }

    @Test
    public void testAddFromTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext itemStateContext = new ItemStateContext(null, listId);
        itemStateContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, without dish_id or list_id
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());
    }

    @Test
    public void testDishItemNoQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tag = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tag);

        // adding a dish  as a new list item
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);


        // we expect correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // we expect 1 detail, with the dish id set
        // and no quantities
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());
    }

    @Test
    public void testDishItemQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a dish item as a new list item
        TagEntity tag = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");

        // adding a dish with quantities as a new list item
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

        // we expect correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());

        // we expect 1 detail, with the dish id set
        // and  quantities
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(),detail.getOriginalQuantity());
        Assertions.assertEquals(dishItem.getWholeQuantity(),detail.getOriginalWholeQuantity());
        Assertions.assertEquals(dishItem.getFractionalQuantity(),detail.getOriginalFractionalQuantity());
        Assertions.assertEquals(dishItem.getUnitSize(),detail.getUnitSize());
        Assertions.assertEquals(dishItem.getUnitId(),detail.getOriginalUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(),detail.getRawEntry());
        Assertions.assertEquals(dishItem.getMarker(),detail.getMarker());


    }

    @Test
    public void testListItemNoQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        ListItemEntity toAdd = createListItem(addedItemsList,tag);

        // so - list is empty (no existing item), and adding list item from different list
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(toAdd);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

        // we expect correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // we expect 1 detail, with the list id set
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(addedItemsList.getId(),detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertNull(detail.getLinkedDishId());
    }

    @Test
    public void testListItemQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        // the item to be added will contain a dish detail, with quantities
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");
        // we'll use the statemachine to create the start state
        ItemStateContext testContext = new ItemStateContext(null, addedItemsList.getId());
        testContext.setDishItem(dishItem);
        ListItemEntity toAdd = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext);

        // so now, the call we're testing - adding the list item toAdd
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(toAdd);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

        // we expect correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // we expect 1 detail, with the list id set and the dish id set
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(),detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedDishId());
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(),detail.getOriginalQuantity());
        Assertions.assertEquals(dishItem.getWholeQuantity(),detail.getOriginalWholeQuantity());
        Assertions.assertEquals(dishItem.getFractionalQuantity(),detail.getOriginalFractionalQuantity());
        Assertions.assertEquals(dishItem.getUnitSize(),detail.getUnitSize());
        Assertions.assertEquals(dishItem.getUnitId(),detail.getOriginalUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(),detail.getRawEntry());
        Assertions.assertEquals(dishItem.getMarker(),detail.getMarker());
    }

    @Test
    public void testListItemComplex() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        // the item to be added will contain a dish detail, with quantities
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");
        // we'll use the statemachine to create the start state
        ItemStateContext testContext = new ItemStateContext(null, addedItemsList.getId());
        testContext.setDishItem(dishItem);
        ListItemEntity toAdd = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext);
        // and now we'll add just a simple tag
        testContext = new ItemStateContext(toAdd, addedItemsList.getId());
        testContext.setTag(tag);
        ListItemEntity complex = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext);

        // so now, the call we're testing - adding the list item toAdd
        // toAdd has a "normal" tag item, and a dishitem with quantity
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(complex);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context);

        // we expect correct dates
        verifyDates(result);
        dateInLastSecond(result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // we expect 2 details
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(2, result.getDetails().size());
        // get detail for dish item
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> d.getLinkedDishId() != null)
                .findFirst().orElse(null);
        Assertions.assertNotNull(detail);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(),detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedDishId());
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(),detail.getOriginalQuantity());
        Assertions.assertEquals(dishItem.getWholeQuantity(),detail.getOriginalWholeQuantity());
        Assertions.assertEquals(dishItem.getFractionalQuantity(),detail.getOriginalFractionalQuantity());
        Assertions.assertEquals(dishItem.getUnitSize(),detail.getUnitSize());
        Assertions.assertEquals(dishItem.getUnitId(),detail.getOriginalUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(),detail.getRawEntry());
        Assertions.assertEquals(dishItem.getMarker(),detail.getMarker());

        // get "plain" detail (no dish id, but list id)
        detail = result.getDetails().stream()
                .filter(d -> d.getLinkedDishId() == null)
                .findFirst().orElse(null);
        Assertions.assertNotNull(detail);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(),detail.getLinkedListId());
        Assertions.assertNull(detail.getLinkedDishId());
    }

    @Test
    public void testAddTagExistingRemoved() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        Long dishId = 12345L;
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setRemovedOn(new Date());
        existing.setUsedCount(0);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 1
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());

    }


    @Test
    public void testDishItemExistingRemoved() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        Long dishId = 12345L;
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        DishItemEntity dishItem = createDishItem(dishId,tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setRemovedOn(new Date());
        existing.setUsedCount(0);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 1
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(2, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> dishId.equals(d.getLinkedDishId()))
                .findFirst().orElse(null);
        Assertions.assertEquals(1, detail.getCount());
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());

    }

    @Test
    public void testListItemExistingRemoved() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        ShoppingListEntity addedFromList = createShoppingList();
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();

        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setRemovedOn(new Date());
        existing.setUsedCount(0);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ListItemEntity listItem = createListItem(addedFromList,tagEntity);
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setListItem(listItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 1
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(2, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> addedFromList.getId().equals(d.getLinkedListId()))
                .findFirst().orElse(null);
        Assertions.assertEquals(1, detail.getCount());
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(addedFromList.getId(),detail.getLinkedListId());

    }

    @Test
    public void testAddTagExisting() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setUsedCount(1);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 2
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());

    }

    @Test
    public void testDishItemExistingOverlap() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        Long dishId = 12345L;
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        DishItemEntity dishItem = createDishItem(dishId,tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setDishItem(dishItem);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 1
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> dishId.equals(d.getLinkedDishId()))
                .findFirst().orElse(null);
        Assertions.assertEquals(2, detail.getCount());
        Assertions.assertEquals(dishId,detail.getLinkedDishId());
        Assertions.assertEquals(listId,detail.getLinkedListId());


    }

    @Test
    public void testListItemExistingOverlap() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        ShoppingListEntity addedFromList = createShoppingList();
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();

        ListItemEntity listItem = createListItem(addedFromList,tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setListItem(listItem);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setListItem(listItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext);

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        dateInLastSecond(result.getUpdatedOn());
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 2
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(2, detail.getCount());
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(addedFromList.getId(),detail.getLinkedListId());

    }


    private ListItemDetailEntity createDetailItem(ListItemEntity item) {
            ListItemDetailEntity detail = new ListItemDetailEntity();
            detail.setItem(item);
            return itemDetailRepository.save(detail);
    }

    private Date calculateYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return Date.from(yesterday.atStartOfDay().toInstant(ZoneOffset.MIN));
    }

    private ListItemEntity createListItem(ShoppingListEntity shoppingListEntity, TagEntity tag) {
        ListItemEntity listItemEntity = new ListItemEntity();
        listItemEntity.setTagId(tag.getId());
        listItemEntity.setTag(tag);
        listItemEntity.setListId(shoppingListEntity.getId());
        ListItemEntity created =  listItemRepository.save(listItemEntity);
        ListItemDetailEntity detail = createDetailItem(created);
        detail.setLinkedListId(shoppingListEntity.getId());
        created.addDetailToItem(detail);
        listItemRepository.save(created);
        return created;
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

    private void verifyDates(ListItemEntity result) {
        Assertions.assertNull(result.getRemovedOn());
        Assertions.assertNotNull(result.getUpdatedOn());
        Assertions.assertNotNull(result.getAddedOn());
    }

    private void dateInLastSecond(Date toCheck) {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        LocalDateTime timeToCheck = LocalDateTime.ofInstant(toCheck.toInstant(), ZoneId.systemDefault());
        System.out.println(oneSecondAgo);
        System.out.println(timeToCheck);
        Assertions.assertTrue(timeToCheck.isAfter(oneSecondAgo));

    }
}
