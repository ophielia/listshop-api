/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list.state;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.list.v2.ShoppingListService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = {"/com/meg/listshop/lmt/list/state/StateMachineRemoveLinkTransitionTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/com/meg/listshop/lmt/list/state/StateMachineRemoveLinkTransitionTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StateMachineRemoveLinkTransitionTest {


    /*
    some test cases
    //test remove -
        no existing -
            one detail, with dish_id
            one detail, no dish_id,
            two details - dish, no dish_id,
        existing
            one detail, with dish_id
            one detail, no dish_id,
            two details - dish, no dish_id,
             */
    private static final Long USER_ID = 500L;
    private static final Long LIST_ID_TO_REMOVE = 33333L;
    private static final Long LIST_1_ID = 88888L;
    private static final Long LIST_2_ID = 77777L;
    private static final Long LIST_3_ID = 66666L;
    private static final Long LIST_4_ID = 55555L;
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @Autowired
    private ListItemStateMachine listItemStateMachine;
    @Autowired
    private ListItemRepository listItemRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testSimpleRemoveLinkListLink() {
        // remove links for list2
        removeLinkInTransaction(LIST_2_ID, LIST_ID_TO_REMOVE);
        // re-retrive - and test no old linked ids
        List<ListItemEntity>        listItems = listItemRepository.findWithDetailsByListId(LIST_1_ID);
        listItems.stream()
                .flatMap(i -> i.getDetails().stream())
                .forEach(detail -> {
                    Assertions.assertFalse(detail == null);
                    Assertions.assertFalse(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_1_ID));
                });
    }

    @Test
    void testSimpleRemoveLinkListLinkWithExisting() {
        // remove links for list2
        removeLinkInTransaction(LIST_2_ID, LIST_ID_TO_REMOVE);

        // re-retrive - and test no old linked ids
        List<ListItemEntity> listItems = listItemRepository.findWithDetailsByListId(LIST_2_ID);
        listItems.stream()
                .flatMap(i -> i.getDetails().stream())
                .forEach(detail -> {
                    Assertions.assertFalse(detail == null);
                    Assertions.assertFalse(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_ID_TO_REMOVE));
                    Assertions.assertTrue(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_2_ID));
                    Assertions.assertEquals(3.0,detail.getQuantity());
                    Assertions.assertNull(detail.getFractionalQuantity());
                });
    }

    @Test
    void testRemoveLinkListLinkWithExistingDishAndList()  {
        // remove links for list3
        removeLinkInTransaction(LIST_3_ID, LIST_ID_TO_REMOVE);

        // re-retrive - and test no old linked ids
        List<ListItemEntity> listItems = listItemRepository.findWithDetailsByListId(LIST_3_ID);
        listItems.stream()
                .flatMap(i -> i.getDetails().stream())
                .forEach(detail -> {
                    Assertions.assertFalse(detail == null);
                    Assertions.assertTrue(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_3_ID));
                    Assertions.assertFalse(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_ID_TO_REMOVE));
                });
    }

    @Test
    void testRemoveLinkListLinkWithMixedAmounts()  {
        // remove links for list3
        removeLinkInTransaction(LIST_4_ID, LIST_ID_TO_REMOVE);

        // re-retrive - and test no old linked ids
        List<ListItemEntity> listItems = listItemRepository.findWithDetailsByListId(LIST_4_ID);
        listItems.stream()
                .flatMap(i -> i.getDetails().stream())
                .forEach(detail -> {
                    Assertions.assertFalse(detail == null);
                    Assertions.assertTrue(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_4_ID));
                    Assertions.assertFalse(detail.getLinkedListId() != null && detail.getLinkedListId().equals(LIST_ID_TO_REMOVE));
                    Assertions.assertTrue(detail.isContainsUnspecified());
                    Assertions.assertEquals(2,detail.getCount());
                });
    }


    void removeLinkInTransaction(Long listId, Long listIdToRemove)  {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            List<ListItemEntity> listItems = listItemRepository.findWithDetailsByListId(listId);
            for (ListItemEntity item: listItems) {

                ItemStateContext context = new ItemStateContext(item, null);
                context.setRemoveListLinkId(listIdToRemove);
                context.setTag(item.getTag());

                try {
                    listItemStateMachine.handleEvent(ListItemEvent.REMOVE_LINK, context, USER_ID);
                } catch (ItemProcessingException e) {
                    throw new RuntimeException(e);
                }

            }

        });
    }

}
