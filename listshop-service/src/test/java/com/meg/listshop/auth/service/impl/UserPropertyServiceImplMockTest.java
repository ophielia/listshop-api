/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.data.repository.UserPropertyRepository;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UserPropertyServiceImplMockTest {

    private UserPropertyService userPropertyService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserPropertyRepository userPropertyRepository;

    @Before
    public void setUp() {
        userPropertyService = new UserPropertyServiceImpl(userService, userPropertyRepository);
    }

    @Test
    public void testGetPropertyForUser() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property = buildUserPropertyEntity(testUser, "key1", "value1");

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserIdAndPropertyKey(TestConstants.USER_3_ID, "key2")).thenReturn(Optional.of(property));

        UserPropertyEntity result = userPropertyService.getPropertyForUser(TestConstants.USER_3_NAME, "key2");

        assertNotNull(result);
        assertEquals("has key1 as key", "key1", property.getKey());
        assertEquals("has value1 as value", "value1", property.getValue());

    }

    @Test
    public void testGetPropertiesForUser() {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        List<UserPropertyEntity> propertyList = Arrays.asList(property1, property2);

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(propertyList);

        List<UserPropertyEntity> resultList = userPropertyService.getPropertiesForUser(TestConstants.USER_3_NAME);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals("List should have 2 elements", 2, resultList.size());
        Map<String, String> resultMap = new HashMap<String, String>();
        resultList.stream()
                .forEach(element -> resultMap.put(element.getKey(), element.getValue()));
        assertTrue("results contain first key", resultMap.containsKey("key1"));
        assertEquals("key 1 has value1 for value", "value1", resultMap.get("key1"));
        assertTrue("results contain second key", resultMap.containsKey("key1"));
        assertEquals("key 2 has value2 for value", "value2", resultMap.get("key2"));

    }

    @Test
    public void testSetPropertiesForUser() {
    }

    // to test
    // set both - standard - none exist
    // set one exists, one doesn't
    // set both exist
    // get property for nonexistant returns null, no error
    // get properties when user doesn't have any returns null, no error


    private UserPropertyEntity buildUserPropertyEntity(UserEntity user, String key, String value) {
        UserPropertyEntity newEntity = new UserPropertyEntity();
        newEntity.setUser(user);
        newEntity.setKey(key);
        newEntity.setValue(value);
        return newEntity;
    }


}