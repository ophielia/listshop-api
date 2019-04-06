package com.meg.atable.auth.service.impl;

import com.meg.atable.Application;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.test.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        UserEntity testUser = userService.getUserById(TestConstants.USER_3_ID);
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        assertEquals(TestConstants.USER_3_ID, testUser.getId());
        assertEquals(TestConstants.USER_3_NAME, testUser.getEmail());

    }

    @Test
    public void testGetUserByUserName() {
        UserEntity testUser = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
        assertEquals(TestConstants.USER_3_ID, testUser.getId());
        assertEquals(TestConstants.USER_3_NAME, testUser.getEmail());

    }

    @Test
    public void testSave() {
        final String testName = "email@test.com";
        UserEntity testUser = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        testUser.setEmail("email@test.com");
        userService.save(testUser);
        UserEntity testResult = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        assertEquals(testName, testResult.getEmail());
    }

    @Test
    public void testCreateUser() {
        final String username = "george";
        final String email = "george@will.run";
        final String password = "Passw0rd";

        UserEntity result = userService.createUser(username, email, password);
        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertNotNull(result.getCreationDate());
        assertNotEquals(result.getPassword(), password);
        assertEquals("$2a$10$NH7iIw4T517hii0kjk/RruJ5n3Puq4k/PkAZLPPZM.KT9ONHspwRm",result.getPassword());
    }
}