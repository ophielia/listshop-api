package com.meg.listshop.lmt.service;

import com.meg.listshop.Application;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/MergeItemCollectorTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MergeItemCollectorTest {

    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private TagService tagService;


    @Test
    public void testLoadTestList() {
        ShoppingListEntity listEntity = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems());
        // blow up test
        Assert.assertTrue(1 == 1);
    }


    @Test
    public void testMergeWithEmpty() {
        ShoppingListEntity listEntity = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems());

        collector.addMergeItems(new ArrayList<>());

        Assert.assertFalse(collector.hasChanges());
        Assert.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    public void testUpdatesToItem() {
        ShoppingListEntity listEntity = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems());
        ItemEntity updated = copyItemForTagId(501L, listEntity.getItems());
        updated.setUpdatedOn(new Date());
        List<ItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assert.assertTrue(collector.hasChanges());
        Assert.assertEquals(1, collector.getChangedItems().size());
        Assert.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    public void testUpdatesToItemServerMoreRecent() {
        ShoppingListEntity listEntity = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems());
        ItemEntity updated = copyItemForTagId(501L, listEntity.getItems());
        LocalDateTime dateTime = LocalDateTime.now().minusDays(22L);
        updated.setUpdatedOn(java.sql.Timestamp.valueOf(dateTime));
        List<ItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assert.assertFalse(collector.hasChanges());
        Assert.assertEquals(0, collector.getChangedItems().size());
        Assert.assertEquals(4, collector.getAllItems().size());
    }

    @Test
    public void testAddingNewItem() {
        ShoppingListEntity listEntity = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);

        MergeItemCollector collector = new MergeItemCollector(5000L, listEntity.getItems());
        ItemEntity updated = createItemForTagId(45L);
        List<ItemEntity> mergeItems = new ArrayList<>();
        mergeItems.add(updated);

        collector.addMergeItems(mergeItems);

        Assert.assertTrue(collector.hasChanges());
        Assert.assertEquals(1, collector.getChangedItems().size());
        Assert.assertEquals(5, collector.getAllItems().size());
    }

    private ItemEntity createItemForTagId(long tagId) {
        TagEntity tagEntity = tagService.getTagById(tagId);
        ItemEntity updated = new ItemEntity();
        updated.setTag(tagEntity);
        return updated;
    }

    private ItemEntity copyItemForTagId(long tagId, List<ItemEntity> items) {
        ItemEntity copyFrom = items.stream().filter(i -> i.getTag().getId().equals(tagId)).findFirst().get();
        ItemEntity returnItem = new ItemEntity();
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