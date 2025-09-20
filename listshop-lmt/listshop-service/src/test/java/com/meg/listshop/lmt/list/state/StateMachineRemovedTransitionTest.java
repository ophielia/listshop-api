package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
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
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StateMachineRemovedTransitionTest {

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
    public void testRemoveByTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();
        Long listId = targetList.getId();
        // setup - adding a simple list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // test context
        ItemStateContext testContext = new ItemStateContext(null, listId);
        testContext.setTag(tagEntity);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, testContext);

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
