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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserPropertyServiceImplMockTest {

    private UserPropertyService userPropertyService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserPropertyRepository userPropertyRepository;

    @BeforeEach
    public void setUp() {
        userPropertyService = new UserPropertyServiceImpl(userService, userPropertyRepository);
    }

    @Test
    void testGetPropertyForUser() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property = buildUserPropertyEntity(testUser, "key1", "value1");

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserIdAndKey(TestConstants.USER_3_ID, "key2")).thenReturn(Optional.of(property));

        UserPropertyEntity result = userPropertyService.getPropertyForUser(TestConstants.USER_3_NAME, "key2");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("key1", property.getKey(), "has key1 as key");
        Assertions.assertEquals("value1", property.getValue(), "has value1 as value");

    }

    @Test
    void testGetPropertyForUser_DoesntExist() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property = buildUserPropertyEntity(testUser, "key1", "value1");

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserIdAndKey(TestConstants.USER_3_ID, "key2")).thenReturn(Optional.ofNullable(null));

        UserPropertyEntity result = userPropertyService.getPropertyForUser(TestConstants.USER_3_NAME, "key2");

        Assertions.assertNull(result);
    }

    @Test
    void testGetPropertiesForUser() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        UserPropertyEntity property1 = buildUserPropertyEntity(testUser, "key1", "value1");
        UserPropertyEntity property2 = buildUserPropertyEntity(testUser, "key2", "value2");
        List<UserPropertyEntity> propertyList = Arrays.asList(property1, property2);

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(propertyList);

        List<UserPropertyEntity> resultList = userPropertyService.getPropertiesForUser(TestConstants.USER_3_NAME);

        Assertions.assertNotNull(resultList);
        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(2, resultList.size(), "List should have 2 elements");
        Map<String, String> resultMap = new HashMap<String, String>();
        resultList.stream()
                .forEach(element -> resultMap.put(element.getKey(), element.getValue()));
        Assertions.assertTrue(resultMap.containsKey("key1"), "results contain first key");
        Assertions.assertEquals("value1", resultMap.get("key1"), "key 1 has value1 for value");
        Assertions.assertTrue(resultMap.containsKey("key1"), "results contain second key");
        Assertions.assertEquals("value2", resultMap.get("key2"), "key 2 has value2 for value");

    }


    @Test
    void testGetPropertiesForUser_NoExisting() throws BadParameterException {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);

        Mockito.when(userService.getUserByUserEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userPropertyRepository.findByUserId(TestConstants.USER_3_ID)).thenReturn(Collections.EMPTY_LIST);

        List<UserPropertyEntity> resultList = userPropertyService.getPropertiesForUser(TestConstants.USER_3_NAME);

        Assertions.assertNotNull(resultList);
        Assertions.assertTrue(resultList.isEmpty());

    }

    @Test
    void testSetPropertiesForUser() throws BadParameterException {
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

        Assertions.assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        Assertions.assertNotNull(savedProperties);
        Assertions.assertFalse(savedProperties.isEmpty());
        Assertions.assertEquals(2, savedProperties.size(), "size is 2");
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        Assertions.assertTrue(resultMap.containsKey("key1"), "key1 exists");
        Assertions.assertEquals("value1", resultMap.get("key1").getValue(), "key1 value is value1");
        Assertions.assertTrue(resultMap.containsKey("key2"), "key2 exists");
        Assertions.assertEquals("value2", resultMap.get("key2").getValue(), "key2 value is value1");

    }

    @Test
    void testSetPropertiesForUser_OneExisting() throws BadParameterException {
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

        Assertions.assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        Assertions.assertNotNull(savedProperties);
        Assertions.assertFalse(savedProperties.isEmpty());
        Assertions.assertEquals(2, savedProperties.size(), "size is 2");
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        Assertions.assertTrue(resultMap.containsKey("key1"), "key1 exists");
        Assertions.assertEquals("crazy new value", resultMap.get("key1").getValue(), "key1 value is value1");
        Assertions.assertTrue(resultMap.containsKey("key2"), "key2 exists");
        Assertions.assertEquals("value2", resultMap.get("key2").getValue(), "key2 value is value1");

    }

    @Test
    void testSetPropertiesForUser_BothExisting() throws BadParameterException {
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

        Assertions.assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        Assertions.assertNotNull(savedProperties);
        Assertions.assertFalse(savedProperties.isEmpty());
        Assertions.assertEquals(2, savedProperties.size(), "size is 2");
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        Assertions.assertTrue(resultMap.containsKey("key1"), "key1 exists");
        Assertions.assertEquals("crazy new value", resultMap.get("key1").getValue(), "key1 value is value1");
        Assertions.assertTrue(resultMap.containsKey("key2"), "key2 exists");
        Assertions.assertEquals("calm value", resultMap.get("key2").getValue(), "key2 value is value1");

    }

    @Test
    void testSetPropertiesForUser_Delete() throws BadParameterException {
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

        Assertions.assertNotNull(saveCapture);
        List<UserPropertyEntity> savedProperties = saveCapture.getValue();
        Assertions.assertNotNull(savedProperties);
        Assertions.assertFalse(savedProperties.isEmpty());
        Assertions.assertEquals(1, savedProperties.size(), "size is 1");
        Map<String, UserPropertyEntity> resultMap = savedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        Assertions.assertTrue(resultMap.containsKey("key2"), "key2 exists");
        Assertions.assertEquals("calm value", resultMap.get("key2").getValue(), "key2 value is value1");

        Assertions.assertNotNull(deleteCapture);
        List<UserPropertyEntity> deletedProperties = deleteCapture.getValue();
        Assertions.assertNotNull(deletedProperties);
        Assertions.assertFalse(deletedProperties.isEmpty());
        Assertions.assertEquals(1, deletedProperties.size(), "size is 1");
        Map<String, UserPropertyEntity> deletedResults = deletedProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));
        Assertions.assertTrue(deletedResults.containsKey("key1"), "key1 exists");

    }


    private UserPropertyEntity buildUserPropertyEntity(UserEntity user, String key, String value) {
        UserPropertyEntity newEntity = new UserPropertyEntity();
        newEntity.setUser(user);
        newEntity.setKey(key);
        newEntity.setValue(value);
        return newEntity;
    }


}
