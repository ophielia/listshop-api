package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.tag.AutoTagService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class AutoTagServiceImplTest {

    @Autowired
    AutoTagService autoTagService;

    @MockBean
    DishService dishService;

    @Test
    public void getDishesToAutotag() {
        // test max number
        // test limit = 5 (test configuration)
        Mockito.when(dishService.getDishesToAutotag(105L, 5)).thenReturn(new ArrayList<DishEntity>());
        List<DishEntity> result = autoTagService.getDishesToAutotag(5);
        Assert.assertTrue(result.isEmpty());

    }
}