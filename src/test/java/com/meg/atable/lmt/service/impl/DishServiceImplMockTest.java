package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.repository.DishRepository;
import com.meg.atable.lmt.service.DishSearchService;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.tag.AutoTagService;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DishServiceImplMockTest {

    private DishService dishService;


    @MockBean
    private DishRepository dishRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AutoTagService autoTagService;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagStructureService tagStructureService;
    @MockBean
    private DishSearchService dishSearchService;


    @Before
    public void setUp() {

        dishService = new DishServiceImpl(dishRepository,
                dishSearchService,
                userRepository,
                autoTagService,
                tagService,
                tagStructureService);
    }


    @Test
    public void testCreateDish() throws Exception {
        String testDishName = "test mock dish";
        DishEntity dish = new DishEntity();
        dish.setDishName(testDishName);
        dish.setUserId(99L);

        ArgumentCaptor<DishEntity> argument = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByUserIdAndDishName(99L, testDishName))
                .thenReturn(new ArrayList<>());

        Mockito.when(dishRepository.save(argument.capture()))
                .thenReturn(dish);

        dishService.create(dish);

        DishEntity dishSaved = argument.getValue();
        Assert.assertEquals(testDishName, dishSaved.getDishName());
        Assert.assertEquals(Long.valueOf(99L), dishSaved.getUserId());

    }

    @Test
    public void testCreateDish_ExistingName() throws Exception {
        String testDishName = "test mock dish";
        DishEntity dish = new DishEntity();
        dish.setDishName(testDishName);
        dish.setUserId(99L);

        // make found duplicate names
        DishEntity existingDish1 = new DishEntity();
        existingDish1.setDishName(testDishName + " " + 2);
        existingDish1.setUserId(99L);
        DishEntity existingDish2 = new DishEntity();
        existingDish2.setDishName(testDishName + " " + 8);
        existingDish2.setUserId(99L);
        DishEntity existingDish3 = new DishEntity();
        existingDish3.setDishName(testDishName + "Abracadabra");
        existingDish3.setUserId(99L);

        ArgumentCaptor<DishEntity> argument = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByUserIdAndDishName(99L, testDishName))
                .thenReturn(Collections.singletonList(dish));
        Mockito.when(dishRepository.findByUserIdAndDishNameLike(99L, testDishName + "%"))
                .thenReturn(Arrays.asList(dish, existingDish1, existingDish2, existingDish3));

        Mockito.when(dishRepository.save(argument.capture()))
                .thenReturn(dish);

        dishService.create(dish);

        DishEntity dishSaved = argument.getValue();
        Assert.assertEquals(testDishName + " " + 4, dishSaved.getDishName());
        Assert.assertEquals(Long.valueOf(99L), dishSaved.getUserId());

    }


}