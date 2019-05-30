package com.meg.atable.lmt.service.task;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.repository.ItemRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 21/03/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StaleItemCleanupTaskTest {


@Autowired
private StaleItemCleanupTask staleItemCleanupTaskTest;

@Autowired
private ItemRepository itemRepository;
@Test
public void testCleanupTask() {
    LocalDate testRemoveDate = LocalDate.now().minusDays(12);

    // 3 items in test data set which are stale
    // get all tags
    List<ItemEntity> allitems = itemRepository.findAll();
    // count them
    long count = allitems.stream().count();
    // retain count of stale items
    List<ItemEntity> staleItems = allitems.subList(0,5);
    for (ItemEntity item : allitems) {
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
    Assert.assertEquals(count - staleCount, newCount);
    }

}