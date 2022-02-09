/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class DishSearchServiceImplTest {

    @Autowired
    private DishSearchService dishSearchService;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Autowired
    private UserService userService;


    private static UserEntity userAccount;


    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
    }

    @Test
    public void findDishesByTag() throws Exception {
        DishSearchCriteria criteria = new DishSearchCriteria(userAccount.getId());
        criteria.setIncludedTagIds(Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID));
        criteria.setExcludedTagIds(Arrays.asList(TestConstants.TAG_4_ID));

        List<DishEntity> dishlist = dishSearchService.findDishes(criteria);
        Assert.assertNotNull(dishlist);
    }


}