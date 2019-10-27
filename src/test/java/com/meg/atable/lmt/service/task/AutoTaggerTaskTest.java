package com.meg.atable.lmt.service.task;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.service.tag.AutoTagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class AutoTaggerTaskTest {

    @MockBean
    AutoTagService autoTagService;

    @Autowired
    AutoTaggerTask autoTaggerTask;

    @Test
    public void autoTagDishes() {
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
        Mockito.verify(autoTagService, times(5)).doAutoTag(any(DishEntity.class), Matchers.eq(false));

    }
}