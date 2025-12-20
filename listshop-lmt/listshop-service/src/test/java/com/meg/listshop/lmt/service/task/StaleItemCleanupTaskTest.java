package com.meg.listshop.lmt.service.task;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by margaretmartin on 21/03/2018.
 */
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class StaleItemCleanupTaskTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private StaleItemCleanupTask staleItemCleanupTaskTest;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testCleanupTask() {
        LocalDate testRemoveDate = LocalDate.now().minusDays(12);

    // 3 items in test data set which are stale
    // get all tags
    List<ListItemEntity> allitems = itemRepository.findAll();
    // count them
    long count = allitems.stream().count();
    // retain count of stale items
    List<ListItemEntity> staleItems = allitems.subList(0,5);
    for (ListItemEntity item : allitems) {
        item.setRemovedOn(java.sql.Date.valueOf(testRemoveDate));
    }
    itemRepository.saveAll(staleItems);

    long staleCount = itemRepository.findByRemovedOnBefore(java.sql.Date.valueOf(LocalDate.now().minusDays(11))).size();

    // call cleanup task
    staleItemCleanupTaskTest.removeItemsByRemovedBeforeDate();

    // get all tags and count them
    allitems = itemRepository.findAll();
    long newCount = allitems.stream().count();

    // count should be 3 less
    Assertions.assertEquals(count - staleCount, newCount);
    }

}
