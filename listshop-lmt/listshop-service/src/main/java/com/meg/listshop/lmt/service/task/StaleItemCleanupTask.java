package com.meg.listshop.lmt.service.task;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
public class StaleItemCleanupTask {

    private static final Logger  logger = LoggerFactory.getLogger(StaleItemCleanupTask.class);


    @Value("${component.staleitemcleanuptask.items.deleted.after.days}")
    int deleteAfterDays = 11;


    ItemRepository itemRepository;

    @Autowired
    public StaleItemCleanupTask(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Scheduled(cron = "0 0 8,13 * * ?")
    public void removeItemsByRemovedBeforeDate() {
        logger.info("About to delete stale tags from item table.");
        LocalDate removedBeforeDate = LocalDate.now().minusDays(deleteAfterDays);

        List<ListItemEntity> itemsToRemove = itemRepository.findByRemovedOnBefore(Date.valueOf(removedBeforeDate));
        int removeCount = itemsToRemove != null ? itemsToRemove.size() : 0;
        logger.info("... found [" + removeCount + "] items to delete.");

        itemRepository.deleteAll(itemsToRemove);
        logger.info("StaleItemCleanupTask complete.");
    }


}
