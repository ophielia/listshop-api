package com.meg.listshop.lmt.list.impl;

import com.meg.listshop.lmt.api.model.ContextType;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.list.ListItemCollector;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.CollectorContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ListItemCollectorTest {


    @Test
    void testAdd() {
        ListItemCollector collector = new ListItemCollector(999L, new ArrayList<>());
        ListItemEntity item = createItem(1L, 100L);

        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified).build();
        // tested method
        collector.addItem(item, context);

        // check results
        List<ListItemEntity> changed = collector.getChangedItems();
        Assertions.assertNotNull(changed);
        Assertions.assertEquals(1, changed.size());

    }

    @Test
    void testUpdate() {

        ListItemEntity item = createItem(1L, 100L);
        ListItemCollector collector = new ListItemCollector(999L, Collections.singletonList(item));

        // tested method
        ListItemEntity itemUpdate = createItem(1L, 100L);
        CollectorContext context = new CollectorContextBuilder().create(ContextType.NonSpecified).build();
        collector.addItem(itemUpdate, context);

        // check results
        List<ListItemEntity> changed = collector.getChangedItems();
        Assertions.assertNotNull(changed);
        Assertions.assertEquals(1, changed.size());
        Assertions.assertNotNull(changed.get(0).getUpdatedOn());

    }

    @Test
    void testDelete() {
        ListItemEntity item = createItem(1L, 100L);
        ListItemCollector collector = new ListItemCollector(999L, Collections.singletonList(item));
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .build();
        // tested method
        collector.removeItemByTagId(1L, context);

        // check results
        List<ListItemEntity> changed = collector.getChangedItems();
        Assertions.assertNotNull(changed);
        Assertions.assertEquals(0, changed.size());
        List<ListItemEntity> removed = collector.getRemovedItems();
        Assertions.assertNotNull(removed);
        Assertions.assertEquals(1, removed.size());
    }


    @Test
    void testAddUpdateDelete() {
        ListItemEntity item = createItem(1L, 100L);
        ListItemEntity item2 = createItem(2L, 200L);
        List<ListItemEntity> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        Date dateCheck = new Date();
        CollectorContext context = new CollectorContextBuilder().create(ContextType.Dish)
                .build();

        ListItemCollector collector = new ListItemCollector(999L, items);

        // tested method(s)
        ListItemEntity item3 = createItem(3L, 300L);
        // add item 3
        collector.addItem(item3, context);
        // remove item
        collector.removeItemByTagId(1L, context);
        // update item 2 (by adding it again)
        collector.addItem(item2, context);

        // check results
        List<ListItemEntity> changed = collector.getChangedItems();
        Assertions.assertNotNull(changed);
        Assertions.assertEquals(2, changed.size());

        // check dates
        int added =0;
        int updated = 0;
        int deleted = 0;
        for (ListItemEntity result : changed) {
            if (result.getRemovedOn() != null) {
                deleted++;
            } else if (result.getUpdatedOn() != null) {
                updated++;
            }
            if (result.getAddedOn() != null) {
                added++;
            }
        }
        Assertions.assertTrue(added > 0);
        Assertions.assertTrue(updated > 0);
    }

    private ListItemEntity createItem(Long tagId, Long itemId) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        ListItemEntity item = new ListItemEntity();
        item.setId(itemId);
        item.setTag(tag);
        return item;

    }
}
