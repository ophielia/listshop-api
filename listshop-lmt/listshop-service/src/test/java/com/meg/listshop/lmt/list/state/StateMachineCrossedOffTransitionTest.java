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
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StateMachineCrossedOffTransitionTest {

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
    public void testCrossOffByTag() throws ItemProcessingException {
        ShoppingListEntity targetList = createShoppingList();

        Long listId = targetList.getId();
        // setup - adding a simple list item from a tag - no relation to other list or dish
        TagEntity tagEntity = createTag();
        ItemStateContext setupContext = new ItemStateContext(null, listId);
        setupContext.setTag(tagEntity);
        ListItemEntity testItem = listItemStateMachine.handleEvent(ListItemEvent.ADD_ITEM, setupContext);

        // test context
        ItemStateContext testContext = new ItemStateContext(testItem, listId);

        ListItemEntity result = listItemStateMachine.handleEvent(ListItemEvent.CROSS_OFF_ITEM, testContext);

        // we expect that the result has the correct dates
        verifyCrossedOffAndUpdated(result);


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



    private void verifyCrossedOffAndUpdated(ListItemEntity result) {
        Assertions.assertNotNull(result.getUpdatedOn());
        dateInLastSecond(result.getUpdatedOn());
        Assertions.assertNotNull(result.getCrossedOff());
        dateInLastSecond(result.getCrossedOff());
    }


    private void dateInLastSecond(Date toCheck) {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        LocalDateTime timeToCheck = LocalDateTime.ofInstant(toCheck.toInstant(), ZoneId.systemDefault());
        System.out.println(oneSecondAgo);
        System.out.println(timeToCheck);
        Assertions.assertTrue(timeToCheck.isAfter(oneSecondAgo));

    }
}
