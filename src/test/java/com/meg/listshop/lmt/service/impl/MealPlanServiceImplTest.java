package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.test.TestConstants;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MealPlanServiceImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private MealPlanService mealPlanService;


    @Autowired
    private UserService userService;


    private UserEntity userAccount;
    private UserEntity deleteUserAccount;
    private UserEntity modifyUserAccount;

    @Autowired
    private DishRepository dishRepository;

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_NAME);
        deleteUserAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        modifyUserAccount = userService.getUserByUserEmail(TestConstants.USER_2_NAME);

    }




}