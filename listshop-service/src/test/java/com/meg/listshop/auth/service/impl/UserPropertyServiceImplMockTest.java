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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Mockito.when(userPropertyRepository.findByUserIdAndKey(TestConstants.USER_3_ID, "key2")).thenReturn(Optional.of(property));

        UserPropertyEntity result = userPropertyService.getPropertyForUser(TestConstants.USER_3_NAME, "key2");

        assertNotNull(result);
        assertEquals("has key1 as key", "key1", property.getKey());
        assertEquals("has value1 as value", "value1", property.getValue());

    }

    @Test
    public void testGetPropertyForUser_DoesntExist() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property = buildUserPropertyEntity(testUser, "key1", "value1");

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserIdAndKey(TestConstants.USER_3_ID, "key2")).thenReturn(Optional.ofNullable(null));

        UserPropertyEntity result = userPropertyService.getPropertyForUser(TestConstants.USER_3_NAME, "key2");

        assertNull(result);
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
    public void testGetPropertiesForUser_NoExisting() {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(Collections.EMPTY_LIST);

        List<UserPropertyEntity> resultList = userPropertyService.getPropertiesForUser(TestConstants.USER_3_NAME);

        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());

    }

    @Test
    public void testSetPropertiesForUser() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        List<UserPropertyEntity> propertyList = Arrays.asList(property1, property2);

        ArgumentCaptor<List<UserPropertyEntity>> saveCapture = ArgumentCaptor.forClass(List.class);
        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(userPropertyRepository.saveAll(saveCapture.capture())).thenReturn(null);

        userPropertyService.setPropertiesForUser(TestConstants.USER_3_NAME, propertyList);

        assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        assertNotNull(savedProperties);
        assertFalse(savedProperties.isEmpty());
        assertEquals("size is 2", 2, savedProperties.size());
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        assertTrue("key1 exists", resultMap.containsKey("key1"));
        assertEquals("key1 value is value1", "value1", resultMap.get("key1").getValue());
        assertTrue("key2 exists", resultMap.containsKey("key2"));
        assertEquals("key2 value is value1", "value2", resultMap.get("key2").getValue());

    }

    @Test
    public void testSetPropertiesForUser_OneExisting() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property1Entry = buildUserPropertyEntity(testUser, "key1", "crazy new value");
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        List<UserPropertyEntity> propertyListEntry = Arrays.asList(property1Entry, property2);
        List<UserPropertyEntity> propertyListExisting = Collections.singletonList(property1);

        ArgumentCaptor<List<UserPropertyEntity>> saveCapture = ArgumentCaptor.forClass(List.class);
        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(propertyListExisting);
        Mockito.when(userPropertyRepository.saveAll(saveCapture.capture())).thenReturn(null);

        userPropertyService.setPropertiesForUser(TestConstants.USER_3_NAME, propertyListEntry);

        assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        assertNotNull(savedProperties);
        assertFalse(savedProperties.isEmpty());
        assertEquals("size is 2", 2, savedProperties.size());
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        assertTrue("key1 exists", resultMap.containsKey("key1"));
        assertEquals("key1 value is value1", "crazy new value", resultMap.get("key1").getValue());
        assertTrue("key2 exists", resultMap.containsKey("key2"));
        assertEquals("key2 value is value1", "value2", resultMap.get("key2").getValue());

    }

    @Test
    public void testSetPropertiesForUser_BothExisting() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property1Entry = buildUserPropertyEntity(testUser, "key1", "crazy new value");
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        UserPropertyEntity property2Entry = buildUserPropertyEntity(testUser, "key2", "calm value");
        List<UserPropertyEntity> propertyListEntry = Arrays.asList(property1Entry, property2Entry);
        List<UserPropertyEntity> propertyListExisting = Arrays.asList(property1, property2);

        ArgumentCaptor<List<UserPropertyEntity>> saveCapture = ArgumentCaptor.forClass(List.class);
        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(propertyListExisting);
        Mockito.when(userPropertyRepository.saveAll(saveCapture.capture())).thenReturn(null);

        userPropertyService.setPropertiesForUser(TestConstants.USER_3_NAME, propertyListEntry);

        assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        assertNotNull(savedProperties);
        assertFalse(savedProperties.isEmpty());
        assertEquals("size is 2", 2, savedProperties.size());
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        assertTrue("key1 exists", resultMap.containsKey("key1"));
        assertEquals("key1 value is value1", "crazy new value", resultMap.get("key1").getValue());
        assertTrue("key2 exists", resultMap.containsKey("key2"));
        assertEquals("key2 value is value1", "calm value", resultMap.get("key2").getValue());

    }

    @Test
    public void testSetPropertiesForUser_Delete() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property1Entry = buildUserPropertyEntity(testUser, "key1", null);
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        UserPropertyEntity property2Entry = buildUserPropertyEntity(testUser, "key2", "calm value");
        List<UserPropertyEntity> propertyListEntry = Arrays.asList(property1Entry, property2Entry);
        List<UserPropertyEntity> propertyListExisting = Arrays.asList(property1, property2);

        ArgumentCaptor<List<UserPropertyEntity>> saveCapture = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<UserPropertyEntity>> deleteCapture = ArgumentCaptor.forClass(List.class);
        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(propertyListExisting);
        Mockito.when(userPropertyRepository.saveAll(saveCapture.capture())).thenReturn(null);
        Mockito.doNothing().when(userPropertyRepository).deleteAll(deleteCapture.capture());

        userPropertyService.setPropertiesForUser(TestConstants.USER_3_NAME, propertyListEntry);

        assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        assertNotNull(savedProperties);
        assertFalse(savedProperties.isEmpty());
        assertEquals("size is 1", 1, savedProperties.size());
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        assertTrue("key2 exists", resultMap.containsKey("key2"));
        assertEquals("key2 value is value1", "calm value", resultMap.get("key2").getValue());

        assertNotNull(deleteCapture);
        List<UserPropertyEntity> deletedProperties = deleteCapture.getValue();
        assertNotNull(deletedProperties);
        assertFalse(deletedProperties.isEmpty());
        assertEquals("size is 1", 1, deletedProperties.size());
        Map<String, UserPropertyEntity> deletedResults = deletedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        assertTrue("key1 exists", deletedResults.containsKey("key1"));

    }


    private UserPropertyEntity buildUserPropertyEntity(UserEntity user, String key, String value) {
        UserPropertyEntity newEntity = new UserPropertyEntity();
        newEntity.setUser(user);
        newEntity.setKey(key);
        newEntity.setValue(value);
        return newEntity;
    }


}