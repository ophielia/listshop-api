package com.meg.listshop.lmt.service.task;

import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
public class AutoTaggerTask {

    private static final Logger logger = LogManager.getLogger(AutoTaggerTask.class);

    @Value("${component.autotaggertask.is.active}")
    boolean taskIsActive;

    @Value("${component.autotaggertask.dish.to.autotag.count}")
    int dishToAutotagCount = 5;

    AutoTagService autoTagService;

    @Autowired
    public AutoTaggerTask(AutoTagService autoTagService) {
        this.autoTagService = autoTagService;
    }

    @Scheduled(fixedDelay = 300000)
    public void autoTagDishes() {
        if (!taskIsActive) {
            return;
        }

        // get dishes to autotag
        List<DishEntity> dishes = autoTagService.getDishesToAutotag(dishToAutotagCount);
        logger.info("About to start autotag task for " + dishToAutotagCount + " dishes.");
        for (DishEntity dish : dishes) {
            autoTagService.doAutoTag(dish, false);
        }
    }


}
