package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.api.model.v2.SpecificationType;
import com.meg.listshop.lmt.conversion.BasicAmount;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;


@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = {"/com/meg/listshop/lmt/list/state/StateMachineActiveTransitionTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StateMachineActiveTransitionTest {

    private static final Long KILO_UNIT_ID = 1014L;
    private static final Long UNIT_UNIT_ID = 1011L;
    private static final Long CUP_UNIT_ID = 1000L;
    private static final Long TAG_FLOUR = 350L;
    private static final Long TAG_TOMATO = 33L;
    private static final Long OZ_UNIT_ID = 1009L;
    private static final Long GRAM_UNIT_ID = 1013L;
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
    void blowUpTest() throws ConversionPathException, ConversionFactorException {
        Assertions.assertTrue(true);
        Assertions.assertNotNull(listItemStateMachine);
    }

    @Test
    void testAddFromTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext itemStateContext = new ItemStateContext(null, listId);
        itemStateContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, without dish_id or list_id
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
    }

    @Test
    void testDishItemNoQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tag = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);

        // adding a dish  as a new list item
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());


        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // we expect 1 detail, with the dish id set
        // and no quantities
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
    }

    @Test
    void testDishItemQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a dish item as a new list item
        TagEntity tag = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");

        // adding a dish with quantities as a new list item
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));

        // we expect 1 detail, with the dish id set
        // and  quantities
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(), detail.getQuantity());
        Assertions.assertEquals(dishItem.getUnitSize(), detail.getUnitSize());
        Assertions.assertEquals(dishItem.getUnitId(), detail.getUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(), detail.getRawEntry());


    }

    @Test
    void testDishItemQuantitiesAddToExisting() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a dish item as a new list item
        TagEntity tag = getTag(TAG_FLOUR);
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("medium");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(CUP_UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");

        // adding a dish with quantities as a new list item
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));

        // we expect 1 detail, with the dish id set
        // and  quantities
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        // verify quantities
        Assertions.assertTrue(detail.getQuantity() > 5.8 && detail.getQuantity() < 5.9);
        Assertions.assertEquals(1009L, detail.getUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(), detail.getRawEntry());
        Assertions.assertEquals(5.875, result.getRoundedQuantity());
        Assertions.assertEquals(FractionType.SevenEighths, result.getFractionalQuantity());
        Assertions.assertEquals(5, result.getWholeQuantity());


        // now, add the same again, and we should have 3 cups
        context = new ItemStateContext(result, listId);
        context.setDishItem(dishItem);
        ListItemEntity secondResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());
        Assertions.assertNotNull(secondResult);
        Assertions.assertEquals(0.75, secondResult.getRoundedQuantity());
        Assertions.assertEquals(FractionType.ThreeQuarters, secondResult.getFractionalQuantity());
        Assertions.assertEquals(0, secondResult.getWholeQuantity());

        // now, add the same a third time for a different dish, but this time, without an amount
        context = new ItemStateContext(result, listId);
        // adding a dish item as a new list item
        DishItemEntity newDishItem = createDishItem(56789L, tag);
        context.setDishItem(newDishItem);
        ListItemEntity thirdResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());
        Assertions.assertNotNull(thirdResult);
        Assertions.assertEquals(0.75, thirdResult.getRoundedQuantity());
        Assertions.assertEquals(FractionType.ThreeQuarters, thirdResult.getFractionalQuantity());
        Assertions.assertEquals(0, thirdResult.getWholeQuantity());
    }


    @Test
    void testListItemNoQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        ListItemEntity toAdd = createListItem(addedItemsList, tag);

        // so - list is empty (no existing item), and adding list item from different list
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(toAdd);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // we expect 1 detail, with the list id set
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(addedItemsList.getId(), detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertNull(detail.getLinkedDishId());
    }

    @Test
    void testListItemQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        // the item to be added will contain a dish detail, with quantities
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");
        // we'll use the statemachine to create the start state
        ItemStateContext testContext = new ItemStateContext(null, addedItemsList.getId());
        testContext.setDishItem(dishItem);
        ListItemEntity toAdd = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext, targetList.getUserId());

        // so now, the call we're testing - adding the list item toAdd
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(toAdd);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // we expect 1 detail, with the list id set and the dish id set
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(), detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedDishId());
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(), detail.getQuantity());
        Assertions.assertEquals(dishItem.getUnitId(), detail.getUnitId());
        Assertions.assertEquals(dishItem.getRawEntry(), detail.getRawEntry());
    }


    @Test
    void testListItemConvertibleQuantities() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = getTag(TAG_FLOUR);
        ShoppingListEntity addedItemsList = createShoppingList();
        // the item to be added will contain a dish detail, with quantities
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(CUP_UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");
        // we'll use the statemachine to create the start state
        ItemStateContext testContext = new ItemStateContext(null, addedItemsList.getId());
        testContext.setDishItem(dishItem);
        ListItemEntity toAdd = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext, targetList.getUserId());

        // so now, the call we're testing - adding the list item toAdd
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(toAdd);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // we expect 1 detail, with the list id set and the dish id set
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(), detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedDishId());
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        // verify item quantities
        Assertions.assertEquals(5, result.getWholeQuantity());
        Assertions.assertEquals(FractionType.SevenEighths, result.getFractionalQuantity());
        Assertions.assertEquals(5.875, result.getRoundedQuantity());
        Assertions.assertEquals(1009L, result.getUnit().getId());
        // verify quantities, detail
        Assertions.assertEquals(5.8608, detail.getQuantity());
        Assertions.assertEquals(1009L, detail.getUnitId());
    }

    @Test
    void testListItemComplex() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // adding a list item as a new list item
        TagEntity tag = createTag();
        ShoppingListEntity addedItemsList = createShoppingList();
        // the item to be added will contain a dish detail, with quantities
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId, tag);
        dishItem.setQuantity(1.5);
        dishItem.setFractionalQuantity(FractionType.OneHalf);
        dishItem.setWholeQuantity(1);
        dishItem.setUnitSize("size");
        dishItem.setRawEntry("rawEntry");
        dishItem.setUnitId(UNIT_UNIT_ID);
        dishItem.setRawModifiers("rawModifiers");
        // we'll use the statemachine to create the start state
        ItemStateContext testContext = new ItemStateContext(null, addedItemsList.getId());
        testContext.setDishItem(dishItem);
        ListItemEntity toAdd = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext, targetList.getUserId());
        // and now we'll add just a simple tag
        testContext = new ItemStateContext(toAdd, addedItemsList.getId());
        testContext.setTag(tag);
        ListItemEntity complex = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext, targetList.getUserId());

        // so now, the call we're testing - adding the list item toAdd
        // toAdd has a "normal" tag item, and a dishitem with quantity
        ItemStateContext context = new ItemStateContext(null, listId);
        context.setListItem(complex);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, context, targetList.getUserId());

        // we expect correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getAddedOn(), 2));
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // we expect 2 details
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(2, result.getDetails().size());
        // get detail for dish item
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> d.getLinkedDishId() != null)
                .findFirst().orElse(null);
        Assertions.assertNotNull(detail);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(), detail.getLinkedListId());
        Assertions.assertNotNull(detail.getLinkedDishId());
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        // verify quantities
        Assertions.assertEquals(dishItem.getQuantity(), detail.getQuantity());
        Assertions.assertEquals(dishItem.getUnitSize(), detail.getUnitSize());
        Assertions.assertEquals(dishItem.getRawEntry(), detail.getRawEntry());
        Assertions.assertEquals(dishItem.getMarker(), detail.getMarker());

        // get "plain" detail (no dish id, but list id)
        detail = result.getDetails().stream()
                .filter(d -> d.getLinkedDishId() == null)
                .findFirst().orElse(null);
        Assertions.assertNotNull(detail);
        Assertions.assertNotNull(detail.getLinkedListId());
        Assertions.assertEquals(addedItemsList.getId(), detail.getLinkedListId());
        Assertions.assertNull(detail.getLinkedDishId());
    }

    @Test
    void testAddTagExisting() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());
        existing.setUsedCount(1);
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 2
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());

    }

    @Test
    void testAddTagWithAmountNonConvertible() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag(); // new tag, can't be converted
        BasicAmount amount = new BasicAmount(1, null, null, UNIT_UNIT_ID, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        setupContext.setTagAmount(amount);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id null
        // quantity of 1, usedCount 1, unitId - unit
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        Assertions.assertEquals(1, detail.getQuantity());
        Assertions.assertEquals(1, detail.getCount());
        Assertions.assertEquals(UNIT_UNIT_ID, detail.getUnitId());
    }

    @Test
    void testAddTagWithAmountSize() throws ItemProcessingException {

        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_TOMATO); // tag flour, which has conversions
        BasicAmount amount = new BasicAmount(1, null, "medium", UNIT_UNIT_ID, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        setupContext.setTagAmount(amount);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        // we expect that the result has the correct dates
        Assertions.assertEquals(1, result.getDetails().size());
        Assertions.assertEquals(1, result.getRoundedQuantity());
        Assertions.assertEquals("medium", result.getUnitSize());

        // now add 1 cup diced - see what happens
        BasicAmount dicedAmount = new BasicAmount(1, "chopped", null, CUP_UNIT_ID, tagEntity);
        ItemStateContext diceyContext = new ItemStateContext(result, listId);
        diceyContext.setTag(tagEntity);
        diceyContext.setTagAmount(dicedAmount);
        ListItemEntity diceyResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, diceyContext, targetList.getUserId());

        Assertions.assertNotNull(diceyResult);
        Assertions.assertEquals(2.216, RoundingUtils.roundToThousandths(diceyResult.getRawQuantity()));
        Assertions.assertEquals(3.0, diceyResult.getRoundedQuantity());
        Assertions.assertEquals(1011L, diceyResult.getUnit().getId());
        // add dish item
        DishItemEntity dishItem = createDishItem(123456L, tagEntity);
        dishItem.setQuantity(0.5);
        dishItem.setUnitId(KILO_UNIT_ID);
        ItemStateContext addDishContext = new ItemStateContext(diceyResult, listId);
        addDishContext.setDishItem(dishItem);
        ListItemEntity twoTagsAndADish = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, addDishContext, targetList.getUserId());

        Assertions.assertNotNull(twoTagsAndADish);
    }


    @Test
    void testAddTagWithAmountConvertible() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_FLOUR); // tag flour, which has conversions
        BasicAmount amount = new BasicAmount(1, null, null, CUP_UNIT_ID, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        setupContext.setTagAmount(amount);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id null
        // quantity of 1, usedCount 1, unitId - unit
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        Assertions.assertEquals(3.907, RoundingUtils.roundToThousandths(detail.getQuantity()));
        Assertions.assertEquals(1, detail.getCount());
        Assertions.assertEquals(OZ_UNIT_ID, detail.getUnitId());
    }

    @Test
    void testAddTagWithAmountConvertibleExisting() throws ItemProcessingException {
        // setup list with an existing item (having an amount)
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_FLOUR); // tag flour, which has conversions
        BasicAmount amount = new BasicAmount(1, null, null, CUP_UNIT_ID, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        setupContext.setTagAmount(amount);
        ListItemEntity setupResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());
        targetList.getItems().add(setupResult);
        shoppingListRepository.save(targetList);

        // now, we'll add flour to the target list again - this time, one ounce
        BasicAmount secondAmount = new BasicAmount(1, null, null, OZ_UNIT_ID, tagEntity);
        ItemStateContext testContext = new ItemStateContext(setupResult, listId);
        testContext.setTag(tagEntity);
        testContext.setTagAmount(secondAmount);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // verify item amounts
        Assertions.assertEquals(5, result.getRoundedQuantity());
        Assertions.assertEquals(OZ_UNIT_ID, result.getUnit().getId());
        // and that the result contains 1 detail, with dish_id null and list_id not null
        // quantity of 4.907, usedCount 2, unitId - ounce
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        Assertions.assertEquals(4.907, RoundingUtils.roundToThousandths(detail.getQuantity()));
        Assertions.assertEquals(2, detail.getCount());
        Assertions.assertEquals(OZ_UNIT_ID, detail.getUnitId());
    }

    @Test
    void testDishItemExistingOverlap() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        Long dishId = 12345L;
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();
        DishItemEntity dishItem = createDishItem(dishId, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setDishItem(dishItem);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, so we have an overlap
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setDishItem(dishItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 1
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().stream()
                .filter(d -> dishId.equals(d.getLinkedDishId()))
                .findFirst().orElse(null);
        Assertions.assertEquals(2, detail.getCount());
        Assertions.assertEquals(dishId, detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());


    }

    @Test
    void testListItemExistingOverlap() throws ItemProcessingException {
        Date addedOn = calculateYesterday();
        ShoppingListEntity addedFromList = createShoppingList();
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = createTag();

        ListItemEntity listItem = createListItem(addedFromList, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setListItem(listItem);
        ListItemEntity existing = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());
        existing.setAddedOn(calculateYesterday());
        targetList.getItems().add(existing);

        // adding an item from a dish - item exists with tag, but it's been removed
        ItemStateContext itemStateContext = new ItemStateContext(existing, listId);
        itemStateContext.setListItem(listItem);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, itemStateContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertEquals(addedOn, result.getAddedOn());
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id, and a count of 2
        Assertions.assertNotNull(result.getDetails());
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertEquals(2, detail.getCount());
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(addedFromList.getId(), detail.getLinkedListId());

    }

    @Test
    void testSpecification() throws ItemProcessingException {
        // add flour tag w/o amount  - should be SpecificationType.NONE
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_FLOUR); // tag flour, which has conversions
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity flourNoAmount = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        Assertions.assertNotNull(flourNoAmount);
        Assertions.assertEquals(SpecificationType.NONE, flourNoAmount.getSpecificationType());
        Assertions.assertEquals(1, flourNoAmount.getDetails().size());
        Assertions.assertTrue(flourNoAmount.getDetails().get(0).isUnspecified());
        Assertions.assertFalse(flourNoAmount.getDetails().get(0).isContainsUnspecified());

        // add flour tag w/amount to same item - should be item SpecificationType.NONE, and 1 item detail with "containsUnspecified"
        ItemStateContext flourWithAmount = new ItemStateContext(flourNoAmount, listId); // adding to existing
        flourWithAmount.setTag(tagEntity);
        BasicAmount oneKiloAmount = new BasicAmount(1, null, null, KILO_UNIT_ID, tagEntity);
        flourWithAmount.setTagAmount(oneKiloAmount);
        ListItemEntity flourWithAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, flourWithAmount, targetList.getUserId());

        Assertions.assertNotNull(flourWithAmountResult);
        Assertions.assertEquals(SpecificationType.MIXED, flourWithAmountResult.getSpecificationType());
        Assertions.assertEquals(1, flourWithAmountResult.getDetails().size());
        Assertions.assertFalse(flourWithAmountResult.getDetails().get(0).isUnspecified());
        Assertions.assertTrue(flourWithAmountResult.getDetails().get(0).isContainsUnspecified());


        // new item - add 1 kg flour - should be SpecificationType.ALL
        ItemStateContext newFlourWithAmount = new ItemStateContext(null, listId); // adding to existing
        newFlourWithAmount.setTag(tagEntity);
        newFlourWithAmount.setTagAmount(oneKiloAmount);
        ListItemEntity newFlourWithAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, newFlourWithAmount, targetList.getUserId());

        Assertions.assertNotNull(newFlourWithAmountResult);
        Assertions.assertEquals(SpecificationType.ALL, newFlourWithAmountResult.getSpecificationType());
        Assertions.assertEquals(1, newFlourWithAmountResult.getDetails().size());
        Assertions.assertFalse(newFlourWithAmountResult.getDetails().get(0).isUnspecified());
        Assertions.assertFalse(newFlourWithAmountResult.getDetails().get(0).isContainsUnspecified());

        // add 1kg flour to tag amount flour (newFlourWithAmount to flourWithAmountResult) - should be SpecificationType.MIXED, and item detail with "containsUnspecified"
        ItemStateContext allFlour = new ItemStateContext(flourWithAmountResult, listId); // adding to existing
        allFlour.setTag(tagEntity);
        allFlour.setTagAmount(oneKiloAmount);
        ListItemEntity allFlourResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, allFlour, targetList.getUserId());

        Assertions.assertNotNull(allFlourResult);
        Assertions.assertEquals(SpecificationType.MIXED, allFlourResult.getSpecificationType());
        Assertions.assertEquals(1, allFlourResult.getDetails().size());
        Assertions.assertFalse(allFlourResult.getDetails().get(0).isUnspecified());
        Assertions.assertTrue(allFlourResult.getDetails().get(0).isContainsUnspecified());
    }

    @Test
    void testText() throws ItemProcessingException {
        // add flour tag w/o amount  - should be empty text
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_FLOUR); // tag flour, which has conversions
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity flourNoAmount = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        Assertions.assertNotNull(flourNoAmount);
        Assertions.assertNotNull(flourNoAmount.getAmountText());
        Assertions.assertTrue(flourNoAmount.getAmountText().isEmpty());

        // add flour tag w/amount to same item - item text should be non empty, detail text should be non empty
        ItemStateContext flourWithAmount = new ItemStateContext(flourNoAmount, listId); // adding to existing
        flourWithAmount.setTag(tagEntity);
        BasicAmount oneKiloAmount = new BasicAmount(1, null, null, KILO_UNIT_ID, tagEntity);
        flourWithAmount.setTagAmount(oneKiloAmount);
        ListItemEntity flourWithAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, flourWithAmount, targetList.getUserId());

        Assertions.assertNotNull(flourWithAmountResult);
        Assertions.assertNotNull(flourWithAmountResult.getAmountText());
        Assertions.assertEquals("2 1/4 lb", flourWithAmountResult.getAmountText());
        ListItemDetailEntity detail = flourWithAmountResult.getDetails().get(0);
        Assertions.assertNotNull(detail.getRawEntry());
        Assertions.assertEquals("2 1/4 lb", detail.getRawEntry());

        // new item -  dish item with given raw amount
        ItemStateContext newFlourWithAmount = new ItemStateContext(null, listId); // adding to existing
        Long dishId = 12345L;
        TagEntity flourTag = getTag(TAG_FLOUR);
        DishItemEntity dishItem = createDishItem(dishId, flourTag);
        dishItem.setRawEntry("some willy wally wang");
        dishItem.setUnitId(CUP_UNIT_ID);
        dishItem.setQuantity(1.5);
        newFlourWithAmount.setDishItem(dishItem);
        ListItemEntity newFlourWithAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, newFlourWithAmount, targetList.getUserId());

        Assertions.assertNotNull(newFlourWithAmountResult);
        Assertions.assertNotNull(newFlourWithAmountResult.getAmountText());
        Assertions.assertEquals("5 7/8 oz", newFlourWithAmountResult.getAmountText());
        ListItemDetailEntity givenAmountDetail = newFlourWithAmountResult.getDetails().get(0);
        Assertions.assertNotNull(givenAmountDetail.getRawEntry());
        Assertions.assertEquals("some willy wally wang", givenAmountDetail.getRawEntry());

        // new item -  dish item with given raw amount
        ItemStateContext dishItemNoRawAmount = new ItemStateContext(null, listId); // adding to existing
        DishItemEntity dishItemNoAmountText = createDishItem(dishId, flourTag);
        dishItemNoAmountText.setUnitId(CUP_UNIT_ID);
        dishItemNoAmountText.setQuantity(1.5);
        dishItemNoRawAmount.setDishItem(dishItemNoAmountText);
        ListItemEntity dishItemNoRawAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, dishItemNoRawAmount, targetList.getUserId());

        Assertions.assertNotNull(dishItemNoRawAmountResult);
        Assertions.assertNotNull(dishItemNoRawAmountResult.getAmountText());
        Assertions.assertEquals("5 7/8 oz", dishItemNoRawAmountResult.getAmountText());
        ListItemDetailEntity datailNoGivenText = dishItemNoRawAmountResult.getDetails().get(0);
        Assertions.assertNotNull(datailNoGivenText.getRawEntry());
        Assertions.assertEquals("5 7/8 oz", datailNoGivenText.getRawEntry());

    }

    @Test
    void testTextMarker() throws ItemProcessingException {
        // add tomato tag w/marker
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        TagEntity tagEntity = getTag(TAG_TOMATO);
        // add tomato tag w/amount to same item - item text should be non empty, detail text should be non empty
        ItemStateContext tomatoWithMarker = new ItemStateContext(null, listId); // adding to existing
        tomatoWithMarker.setTag(tagEntity);
        BasicAmount dicedTomatoes = new BasicAmount(1, "chopped", null, CUP_UNIT_ID, tagEntity);
        tomatoWithMarker.setTagAmount(dicedTomatoes);
        ListItemEntity tomatoWithMarkerResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, tomatoWithMarker, targetList.getUserId());

        Assertions.assertNotNull(tomatoWithMarkerResult);
        Assertions.assertNotNull(tomatoWithMarkerResult.getAmountText());
        Assertions.assertEquals("2 medium", tomatoWithMarkerResult.getAmountText());
        ListItemDetailEntity detail = tomatoWithMarkerResult.getDetails().get(0);
        Assertions.assertNotNull(detail.getRawEntry());
        Assertions.assertEquals("2 medium", detail.getRawEntry());

        // create tomato tag with small amount - at least small unit size -
        // add flour tag w/amount to same item - item text should be non empty, detail text should be non empty
        ItemStateContext lessThanOne = new ItemStateContext(null, listId); // adding to existing
        lessThanOne.setTag(tagEntity);
        BasicAmount halfAKilo = new BasicAmount(0.5, null, null, KILO_UNIT_ID, tagEntity);
        lessThanOne.setTagAmount(halfAKilo);
        ListItemEntity lessThanOneResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, lessThanOne, targetList.getUserId());

        Assertions.assertNotNull(lessThanOneResult);
        Assertions.assertNotNull(lessThanOneResult.getAmountText());
        Assertions.assertEquals("4 medium", lessThanOneResult.getAmountText());
        ListItemDetailEntity lessThanOneDetail = lessThanOneResult.getDetails().get(0);
        Assertions.assertNotNull(lessThanOneDetail.getRawEntry());
        Assertions.assertEquals("4 medium", lessThanOneDetail.getRawEntry());

        ItemStateContext flourWithAmount = new ItemStateContext(null, listId); // adding to existing
        TagEntity flourTag = getTag(TAG_FLOUR);
        flourWithAmount.setTag(flourTag);
        BasicAmount halfKiloAmount = new BasicAmount(0.25, null, null, KILO_UNIT_ID, flourTag);
        flourWithAmount.setTagAmount(halfKiloAmount);
        ListItemEntity flourWithAmountResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, flourWithAmount, targetList.getUserId());

        Assertions.assertNotNull(flourWithAmountResult);
        Assertions.assertNotNull(flourWithAmountResult.getAmountText());
        Assertions.assertEquals("5/8 lb", flourWithAmountResult.getAmountText());
        ListItemDetailEntity flourDetail = flourWithAmountResult.getDetails().get(0);
        Assertions.assertNotNull(flourDetail.getRawEntry());
        Assertions.assertEquals("5/8 lb", flourDetail.getRawEntry());

    }


    @Test
    void testAddTagWithUserDomain() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        targetList.setUserId(34L);
        TagEntity tagEntity = getTag(TAG_FLOUR); // tag flour, which has conversions
        BasicAmount amount = new BasicAmount(1, null, null, CUP_UNIT_ID, tagEntity);
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        setupContext.setTagAmount(amount);
        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, targetList.getUserId());

        // we expect that the result has the correct dates
        verifyDates(result);
        Assertions.assertTrue(ServiceTestUtils.dateInLastXSeconds(result.getUpdatedOn(), 2));
        // and that the result contains 1 detail, with dish_id and list_id null
        // quantity of 1, usedCount 1, unitId - unit
        Assertions.assertEquals(1, result.getDetails().size());
        ListItemDetailEntity detail = result.getDetails().get(0);
        Assertions.assertNull(detail.getLinkedDishId());
        Assertions.assertEquals(listId, detail.getLinkedListId());
        Assertions.assertEquals(111, RoundingUtils.roundToThousandths(detail.getQuantity()));
        Assertions.assertEquals(1, detail.getCount());
        Assertions.assertEquals(GRAM_UNIT_ID, detail.getUnitId());
    }

    @Test
    void testComplexItemsNotTagSpecificTest() throws ItemProcessingException {
        // list one -
        ShoppingListEntity listOne = createShoppingList();
        Long listOneId = listOne.getId();
        listOne.setUserId(34L); // metric user
        TagEntity tagEntity = createTag(); // any tag, no specific conversion
        // add tag, no amount
        ItemStateContext setupContext = new ItemStateContext(null, listOneId);
        setupContext.setTag(tagEntity);
        ListItemEntity listOneItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext, listOne.getUserId());

        // add tag with 100 g
        BasicAmount amount = new BasicAmount(100, null, null, GRAM_UNIT_ID, tagEntity);
        ItemStateContext tagAddContext = new ItemStateContext(listOneItem, listOneId);
        tagAddContext.setTag(tagEntity);
        tagAddContext.setTagAmount(amount);
        listOneItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, tagAddContext, listOne.getUserId());

        // check list one results
        Assertions.assertNotNull(listOneItem);
        Assertions.assertEquals(1, listOneItem.getDetails().size());
        Assertions.assertEquals(100.0, listOneItem.getRoundedQuantity());
        Assertions.assertEquals(100.0, listOneItem.getRawQuantity());
        Assertions.assertEquals(GRAM_UNIT_ID, listOneItem.getUnit().getId());
        Assertions.assertEquals("100 gram", listOneItem.getAmountText());
        Assertions.assertTrue(listOneItem.getDetails().get(0).isContainsUnspecified());
        Assertions.assertFalse(listOneItem.getDetails().get(0).isUnspecified());
        Assertions.assertEquals(SpecificationType.MIXED, listOneItem.getSpecificationType());

        // list two -
        ShoppingListEntity listTwo = createShoppingList();
        Long listTwoId = listTwo.getId();
        listTwo.setUserId(34L); // metric user
        Long dishId1 = 1234L;
        Long dishId2 = 5678L;

        // add dish item, with 3 oz
        DishItemEntity firstDishItem = createDishItem(dishId1, tagEntity);
        ItemStateContext firstDishContext = new ItemStateContext(null, listTwoId);
        firstDishContext.setDishItem(firstDishItem);
        ListItemEntity listTwoItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, firstDishContext, listTwo.getUserId());
        // add different dish item, no amount
        DishItemEntity secondDishItem = createDishItem(dishId2, tagEntity);
        secondDishItem.setQuantity(6.0);
        secondDishItem.setUnitId(OZ_UNIT_ID);
        ItemStateContext secondDishContext = new ItemStateContext(listTwoItem, listTwoId);
        secondDishContext.setDishItem(secondDishItem);
        listTwoItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, secondDishContext, listTwo.getUserId());

        Assertions.assertNotNull(listTwoItem);
        Assertions.assertEquals(2, listTwoItem.getDetails().size());
        Assertions.assertEquals(170.125, listTwoItem.getRoundedQuantity());
        Assertions.assertEquals(GRAM_UNIT_ID, listTwoItem.getUnit().getId());
        Assertions.assertEquals("170 1/8 gram", listTwoItem.getAmountText());
        ListItemDetailEntity firstDetail = listTwoItem.getDetails().stream().filter(d -> d.getLinkedDishId().equals(dishId1)).findFirst().orElse(null);
        Assertions.assertNotNull(firstDetail);
        Assertions.assertTrue(firstDetail.isUnspecified());
        Assertions.assertEquals("", firstDetail.getRawEntry());
        ListItemDetailEntity secondDetail = listTwoItem.getDetails().stream().filter(d -> d.getLinkedDishId().equals(dishId2)).findFirst().orElse(null);
        Assertions.assertNotNull(secondDetail);
        Assertions.assertFalse(secondDetail.isUnspecified());
        Assertions.assertFalse(secondDetail.isContainsUnspecified());
        Assertions.assertEquals("170 1/8 gram", secondDetail.getRawEntry());
        Assertions.assertEquals(SpecificationType.MIXED, listTwoItem.getSpecificationType());

        // add list one to list two
        ItemStateContext finalContext = new ItemStateContext(listTwoItem, listTwoId);
        finalContext.setListItem(listOneItem);
        ListItemEntity finalResult = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, finalContext, listTwo.getUserId());

        // should have 3 details
        Assertions.assertNotNull(finalResult);
        Assertions.assertEquals(3, finalResult.getDetails().size());
        Assertions.assertEquals(270.125, finalResult.getRoundedQuantity());
        Assertions.assertEquals(GRAM_UNIT_ID, finalResult.getUnit().getId());
        Assertions.assertEquals("270 1/8 gram", finalResult.getAmountText());
        firstDetail = finalResult.getDetails().stream().filter(d -> d.getLinkedDishId().equals(dishId1)).findFirst().orElse(null);
        Assertions.assertNotNull(firstDetail);
        Assertions.assertTrue(firstDetail.isUnspecified());
        Assertions.assertEquals("", firstDetail.getRawEntry());
        secondDetail = finalResult.getDetails().stream().filter(d -> d.getLinkedDishId().equals(dishId2)).findFirst().orElse(null);
        Assertions.assertNotNull(secondDetail);
        Assertions.assertFalse(secondDetail.isUnspecified());
        Assertions.assertFalse(secondDetail.isContainsUnspecified());
        Assertions.assertEquals("170 1/8 gram", secondDetail.getRawEntry());
        ListItemDetailEntity thirdDetail = finalResult.getDetails().stream().filter(d -> d.getLinkedListId().equals(listOneId)).findFirst().orElse(null);
        Assertions.assertTrue(thirdDetail.isContainsUnspecified());
        Assertions.assertFalse(thirdDetail.isUnspecified());
        Assertions.assertEquals(100.0, thirdDetail.getQuantity());
        Assertions.assertEquals("100 gram", thirdDetail.getRawEntry());
        Assertions.assertEquals(SpecificationType.MIXED, finalResult.getSpecificationType());
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
        ListItemEntity created = listItemRepository.save(listItemEntity);
        ListItemDetailEntity detail = createDetailItem(created);
        detail.setLinkedListId(shoppingListEntity.getId());
        created.addDetailToItem(detail);
        listItemRepository.save(created);
        return created;
    }

    private ShoppingListEntity createShoppingList() {

        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setName(LocalDateTime.now().toString());
        shoppingListEntity.setUserId(20L);
        return shoppingListRepository.save(shoppingListEntity);
    }

    private TagEntity createTag() {
        TagEntity tag = new TagEntity();
        tag.setName(LocalDateTime.now().toString());
        tag.setTagType(TagType.Ingredient);
        tag.setInternalStatus(TagInternalStatus.EMPTY);
        return tagRepository.save(tag);
    }


    private TagEntity getTag(Long tagId) {
        return tagRepository.findById(tagId).orElse(null);
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

    private void dateInLastTwoSeconds(Date toCheck) {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(2);
        LocalDateTime timeToCheck = LocalDateTime.ofInstant(toCheck.toInstant(), ZoneId.systemDefault());
        System.out.println(oneSecondAgo);
        System.out.println(timeToCheck);
        Assertions.assertTrue(timeToCheck.isAfter(oneSecondAgo));

    }
}
