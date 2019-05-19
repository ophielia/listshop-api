package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ListItemCollectorTest {


    @Test
    public void testAdd() {
        ListItemCollector collector = new ListItemCollector(999L, new ArrayList<>());
        ItemEntity item = createItem(1L, 100L);

        // tested method
        collector.addItem(item);

        // check results
        List<ItemEntity> added = collector.getItemsAdded();
        assertNotNull(added);
        assertEquals(1, added.size());
        List<ItemEntity> removed = collector.getItemsToDelete();
        assertNotNull(removed);
        assertEquals(0, removed.size());
        List<ItemEntity> updated = collector.getItemsToUpdate();
        assertNotNull(updated);
        assertEquals(0, updated.size());
    }

    @Test
    public void testUpdate() {
        ItemEntity item = createItem(1L, 100L);
        ListItemCollector collector = new ListItemCollector(999L, Collections.singletonList(item));

        // tested method
        ItemEntity itemUpdate = createItem(1L, 100L);
        collector.addItem(itemUpdate);

        // check results
        List<ItemEntity> added = collector.getItemsAdded();
        assertNotNull(added);
        assertEquals(0, added.size());
        List<ItemEntity> removed = collector.getItemsToDelete();
        assertNotNull(removed);
        assertEquals(0, removed.size());
        List<ItemEntity> updated = collector.getItemsToUpdate();
        assertNotNull(updated);
        assertEquals(1, updated.size());
    }

    @Test
    public void testDelete() {
        ItemEntity item = createItem(1L, 100L);
        ListItemCollector collector = new ListItemCollector(999L, Collections.singletonList(item));

        // tested method
        collector.removeItemByTagId(1L, null, true);

        // check results
        List<ItemEntity> added = collector.getItemsAdded();
        assertNotNull(added);
        assertEquals(0, added.size());
        List<ItemEntity> removed = collector.getItemsToDelete();
        assertNotNull(removed);
        assertEquals(1, removed.size());
        List<ItemEntity> updated = collector.getItemsToUpdate();
        assertNotNull(updated);
        assertEquals(0, updated.size());
    }


    @Test
    public void testAddUpdateDelete() {
        ItemEntity item = createItem(1L, 100L);
        ItemEntity item2 = createItem(2L, 200L);
        List<ItemEntity> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        Date dateCheck = new Date();

        ListItemCollector collector = new ListItemCollector(999L, items);

        // tested method(s)
        ItemEntity item3 = createItem(3L, 300L);
        // add item 3
        collector.addItem(item3);
        // remove item
        collector.removeItemByTagId(1L, null, true);
        // update item 2 (by adding it again)
        collector.addItem(item2);

        // check results
        List<ItemEntity> added = collector.getItemsAdded();
        assertNotNull(added);
        assertEquals(1, added.size());
        List<ItemEntity> removed = collector.getItemsToDelete();
        assertNotNull(removed);
        assertEquals(1, removed.size());
        List<ItemEntity> updated = collector.getItemsToUpdate();
        assertNotNull(updated);
        assertEquals(1, updated.size());

        // check dates
        for (ItemEntity result : added) {
            assertNotNull(result.getAddedOn());
            assertTrue(result.getAddedOn().after(dateCheck));
        }
        for (ItemEntity result : removed) {
            assertNotNull(result.getRemovedOn());
            assertTrue(result.getRemovedOn().after(dateCheck));
        }
        for (ItemEntity result : updated) {
            assertNotNull(result.getUpdatedOn());
            assertTrue(result.getUpdatedOn().after(dateCheck));
        }
    }

    private ItemEntity createItem(Long tagId, Long itemId) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        ItemEntity item = new ItemEntity();
        item.setId(itemId);
        item.setTag(tag);
        return item;

    }
}