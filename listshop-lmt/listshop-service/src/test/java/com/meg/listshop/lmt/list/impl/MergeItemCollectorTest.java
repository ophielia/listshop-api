/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.list.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.MergeItemCollector;
import com.meg.listshop.lmt.list.ShoppingListService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/MergeItemCollectorTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MergeItemCollectorTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private TagService tagService;


    @Test
    void testLoadTestList() {
        ShoppingListEntity listEntity = shoppingListService.getListForUserById(TestConstants.USER_1_ID, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems(), new Date());
        // blow up test
        Assertions.assertEquals(1, 1);
    }


    @Test
    void testMergeWithEmpty() {
        ShoppingListEntity listEntity = shoppingListService.getListForUserById(TestConstants.USER_1_ID, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems(), new Date());

        collector.addMergeItems(new ArrayList<>());

        Assertions.assertFalse(collector.hasChanges());
        Assertions.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    void testUpdatesToItem() {
        ShoppingListEntity listEntity = shoppingListService.getListForUserById(TestConstants.USER_1_ID, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems(), new Date());
        ListItemEntity updated = copyItemForTagId(501L, listEntity.getItems());
        updated.setUpdatedOn(new Date());
        List<ListItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assertions.assertTrue(collector.hasChanges());
        Assertions.assertEquals(1, collector.getChangedItems().size());
        Assertions.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    void testUpdatesToItemServerMoreRecent() {
        ShoppingListEntity listEntity = shoppingListService.getListForUserById(TestConstants.USER_1_ID, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems(), new Date());
        ListItemEntity updated = copyItemForTagId(501L, listEntity.getItems());
        LocalDateTime dateTime = LocalDateTime.now().minusDays(22L);
        updated.setUpdatedOn(java.sql.Timestamp.valueOf(dateTime));
        List<ListItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assertions.assertFalse(collector.hasChanges());
        Assertions.assertEquals(0, collector.getChangedItems().size());
        Assertions.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    void testAddingNewItem() {
        ShoppingListEntity listEntity = shoppingListService.getListForUserById(TestConstants.USER_1_ID, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems(), new Date());
        ListItemEntity updated = createItemForTagId(45L);
        List<ListItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assertions.assertTrue(collector.hasChanges());
        Assertions.assertEquals(1, collector.getChangedItems().size());
        Assertions.assertEquals(5, collector.getAllItems().size());
    }

    private ListItemEntity createItemForTagId(long tagId) {
        TagEntity tagEntity = tagService.getTagById(tagId);
        ListItemEntity updated = new ListItemEntity();
        updated.setTag(tagEntity);
        return updated;
    }

    private ListItemEntity copyItemForTagId(long tagId, List<ListItemEntity> items) {
        ListItemEntity copyFrom = items.stream().filter(i -> i.getTag().getId().equals(tagId)).findFirst().get();
        ListItemEntity returnItem = new ListItemEntity();
        returnItem.setId(copyFrom.getId());
        returnItem.setListId(copyFrom.getListId());
        returnItem.setAddedOn(copyFrom.getAddedOn());
        returnItem.setUpdatedOn(copyFrom.getUpdatedOn());
        returnItem.setCrossedOff(copyFrom.getCrossedOff());
        returnItem.setRemovedOn(copyFrom.getRemovedOn());
        returnItem.setUsedCount(copyFrom.getUsedCount());
        returnItem.setTag(copyFrom.getTag());
        return returnItem;
    }
}
