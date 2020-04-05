package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.DishService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class DishServiceImplTest {

    @Autowired
    DishRepository dishRepository;

    @Autowired
    DishService dishService;

    @Test
    public void testGetDishesToAutotag() {
        // three dishes in test data with autotag 105 (currently the max)
        // retrieve list of dishes - get count
        List<DishEntity> alldishes = dishRepository.findAll();
        int allDishCount = alldishes.size();
        // retrieve dishestoautotag with statusflag 105 and limit 10000 - get count
        List<DishEntity> autotagDishes = dishService.getDishesToAutotag(105L, 100000);
        int autoTagCount = autotagDishes.size();
        // count autotag should be 3 less than total
        Assert.assertTrue((allDishCount - 3) == autoTagCount);

        // call dishesToAutotag with limit of 10
        // ensure 10 dishes returned.
        List<DishEntity> autotagDishes2 = dishService.getDishesToAutotag(105L, 10);
        Assert.assertEquals(10, autotagDishes2.size());


    }

}