package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;


@ExtendWith(SpringExtension.class)
class CollectedItemTest {

    // test get status date
    @Test
    void testGetStatusDate() {
        LocalDateTime addedDate = LocalDateTime.of(2019, 01, 01, 0, 0);
        LocalDateTime updatedDate = LocalDateTime.of(2019, 02, 02, 0, 0);
        LocalDateTime removedDate = LocalDateTime.of(2019, 03, 03, 0 ,0);
        LocalDateTime crossedOffDate = LocalDateTime.of(2019, 03, 04, 0, 0);

        // item with added only
        ListItemEntity item = new ListItemEntity();
        item.setAddedOn(java.sql.Timestamp.valueOf(addedDate));

        CollectedItem collectedItem = new CollectedItem(item);
        // getStatusDate should return added date
        Assertions.assertEquals(collectedItem.getStatusDate(), addedDate);

        // now add updated
        item.setUpdatedOn(java.sql.Timestamp.valueOf(updatedDate));

        // getStatusDate should return updated
        Assertions.assertEquals(collectedItem.getStatusDate(), updatedDate);

        // now add crossed off
        item.setCrossedOff(java.sql.Timestamp.valueOf(crossedOffDate));

        // getStatusDate should return crossedOfff
        Assertions.assertEquals(collectedItem.getStatusDate(), crossedOffDate);


        // now add crossed off
        item.setRemovedOn(java.sql.Timestamp.valueOf(removedDate));

        // getStatusDate should return crossedOfff
        Assertions.assertEquals(collectedItem.getStatusDate(), removedDate);
    }


    // test get equals
    @Test
    void testDateEquals() {
        TagEntity tagEntity = new TagEntity("test tag", "test tag");

        LocalDateTime firstDate = LocalDateTime.now();
        LocalDateTime closeToFirstDate = firstDate.minusSeconds(1L);
        LocalDateTime notSoCloseToFirstDate = firstDate.minusSeconds(5L);

        ListItemEntity item1 = new ListItemEntity();
        item1.setTag(tagEntity);
        item1.setUpdatedOn(java.sql.Timestamp.valueOf(firstDate));

        ListItemEntity item2 = new ListItemEntity();
        item2.setTag(tagEntity);
        item2.setUpdatedOn(java.sql.Timestamp.valueOf(closeToFirstDate));

        CollectedItem cItem1 = new CollectedItem(item1);
        CollectedItem cItem2 = new CollectedItem(item2);

        Assertions.assertTrue(cItem1.equalsWithWindow(2, cItem2));

        // now, not equals
        item2.setUpdatedOn(java.sql.Timestamp.valueOf(notSoCloseToFirstDate));

        Assertions.assertFalse(cItem1.equalsWithWindow(2, cItem2));
    }

}
