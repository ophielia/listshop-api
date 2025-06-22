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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StateMachineActiveTransitionTest {

    private static final Long LIST_ID = 1234L;
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
        // adding a list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext itemStateContext = new ItemStateContext(null, LIST_ID);
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
        Assertions.assertNull(detail.getLinkedListId());
    }

    @Test
    public void testDishItemNoQuantities() throws ItemProcessingException {
        TagEntity tag = createTag();
        Long dishId = 12345L;
        DishItemEntity dishItem = createDishItem(dishId,tag);

        // adding a dish  as a new list item
        ItemStateContext context = new ItemStateContext(null, LIST_ID);
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
        Assertions.assertNull(detail.getLinkedListId());
    }

    @Test
    public void testDishItemQuantities() throws ItemProcessingException {
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
        ItemStateContext context = new ItemStateContext(null, LIST_ID);
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
        Assertions.assertNull(detail.getLinkedListId());
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
    public void testListItemNoQuantities() {
        // adding a list item as a new list item

        // we expect correct dates

        // we expect 1 detail, with the list id set
        // and no quantities
        Assertions.assertTrue(true);
    }

    @Test
    public void testListItemQuantities() {
        // adding a list item as a new list item

        // we expect correct dates

        // we expect 1 detail, with the list id set
        // and quantities from list item
        Assertions.assertTrue(true);
    }

    @Test
    public void testListItemComplex() {
        // adding a list item as a new list item
        // this time, we're adding a list item which
        // has 3 details itself

        // we expect correct dates

        // we expect 3 details, with the list id set
        // and details matching list item
        Assertions.assertTrue(true);
    }

    @Test
    public void testAddTagExistingRemoved() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testDishItemExistingRemoved() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testListItemExistingRemoved() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testAddTagExisting() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testDishItemExistingNoOverlap() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testListItemExistingNoOverlap() {
        Assertions.assertTrue(true);
    }


    @Test
    public void testDishItemExistingOverlap() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testListItemExistingOverlap() {
        Assertions.assertTrue(true);
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
        return listItemRepository.save(listItemEntity);
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
