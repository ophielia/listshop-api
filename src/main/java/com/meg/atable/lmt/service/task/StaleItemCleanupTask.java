package com.meg.atable.lmt.service.task;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.repository.ItemRepository;
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

    @Value("${component.staleitemcleanuptask.items.deleted.after.days}")
    int deleteAfterDays = 11;

    @Autowired
    ItemRepository itemRepository;

    @Scheduled(cron = "0 0 8,13 * * ?")
    public void removeItemsByRemovedBeforeDate() {
        //MM need logging here
        LocalDate removedBeforeDate = LocalDate.now().minusDays(deleteAfterDays);

        List<ItemEntity> itemsToRemove = itemRepository.findByRemovedOnBefore(Date.valueOf(removedBeforeDate));

        itemRepository.deleteAll(itemsToRemove);
    }


}
