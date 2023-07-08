package com.meg.listshop.lmt.service.campaign.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CampaignServiceImplMockTest {

    @Test
    public void testCreateDish_ExistingName() throws Exception {
/*
        String testDishName = "test mock dish";
        String testUserName = "test@username.com";
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

        dishService.createDish(testUserName, dish);

        DishEntity dishSaved = argument.getValue();
        Assert.assertEquals(testDishName + " " + 4, dishSaved.getDishName());
        Assert.assertEquals(Long.valueOf(99L), dishSaved.getUserId());
 */

    }

    @Test
    public void testGetDishesToAutotag() {
        Long statusFlag = 105L;
        int dishLimit = 15;

        Pageable expectedPageable = PageRequest.of(0, dishLimit);

        // dishService.getDishesToAutotag(statusFlag, dishLimit);

        //Mockito.verify(dishRepository, Mockito.times(1)).findDishesToAutotag(statusFlag, expectedPageable);


    }

}