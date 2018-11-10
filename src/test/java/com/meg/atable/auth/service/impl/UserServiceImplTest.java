package com.meg.atable.auth.service.impl;

import com.meg.atable.Application;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.test.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        UserAccountEntity testUser = userService.getUserById(TestConstants.USER_3_ID);
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        assertEquals(TestConstants.USER_3_ID, testUser.getId());
        assertEquals(TestConstants.USER_3_NAME, testUser.getUsername());

    }

    @Test
    public void testGetUserByUserName() {
        UserAccountEntity testUser = userService.getUserByUserName(TestConstants.USER_3_NAME);
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        assertEquals(TestConstants.USER_3_ID, testUser.getId());
        assertEquals(TestConstants.USER_3_NAME, testUser.getUsername());

    }

    @Test
    public void testSave() {
        final String testName = "email@test.com";
        UserAccountEntity testUser = userService.getUserByUserName(TestConstants.USER_3_NAME);
        testUser.setEmail("email@test.com");
        userService.save(testUser);
        UserAccountEntity testResult = userService.getUserByUserName(TestConstants.USER_3_NAME);
        assertEquals(testName, testResult.getEmail());
    }

    @Test
    public void deleteAll() {
    }
}