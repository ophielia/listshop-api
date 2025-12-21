/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.dish.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.dish.DishSearchCriteria;
import com.meg.listshop.lmt.dish.DishSearchService;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Testcontainers
class DishSearchServiceImplTest {

    @Autowired
    private DishSearchService dishSearchService;


    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Autowired
    private UserService userService;


    private static UserEntity userAccount;


    @BeforeEach
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
    }

    @Test
    void findDishesByTag() throws Exception {
        DishSearchCriteria criteria = new DishSearchCriteria(userAccount.getId());
        criteria.setIncludedTagIds(Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID));
        criteria.setExcludedTagIds(Arrays.asList(TestConstants.TAG_4_ID));

        List<DishEntity> dishlist = dishSearchService.findDishes(criteria);
        Assertions.assertNotNull(dishlist);
    }


}
