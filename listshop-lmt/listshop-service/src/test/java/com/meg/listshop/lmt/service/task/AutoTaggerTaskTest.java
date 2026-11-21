/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service.task;

import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AutoTaggerTaskTest {

    @MockitoBean
    AutoTagService autoTagService;

    AutoTaggerTask autoTaggerTask;

    @BeforeEach
    void setUp() {

        autoTaggerTask = new AutoTaggerTask(autoTagService);
    }

    @Test
    void autoTagDishes() {
        autoTaggerTask.taskIsActive = true;
        List<DishEntity> dummyDishList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dummyDishList.add(new DishEntity());
        }

        Mockito.when(autoTagService.getDishesToAutotag(5))
                .thenReturn(dummyDishList);
        // call under test
        autoTaggerTask.autoTagDishes();
        // verification
        Mockito.verify(autoTagService, times(5)).doAutoTag(any(DishEntity.class), ArgumentMatchers.eq(false));

    }
}
