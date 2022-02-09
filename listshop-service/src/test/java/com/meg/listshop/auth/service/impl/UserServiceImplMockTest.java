/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.DateUtils;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UserServiceImplMockTest {

    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserDeviceRepository userDeviceRepository;
    @MockBean
    private AuthorityRepository authorityRepository;

    private final String buildNumber = "buildNumber";
    private final String clientVersion = "clientVersion";
    private final ClientType clientType = ClientType.Mobile;
    private final String deviceId = "deviceId";
    private final String model = "model";
    private final String name = "name";
    private final String ossystem = "os";
    private final String osversion = "osversion";

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userDeviceRepository, authorityRepository);
    }

    @Test
    public void testGetUserById() {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);
        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.of(testUser));

        UserEntity resultUser = userService.getUserById(TestConstants.USER_3_ID);
        assertNotNull(resultUser);
        assertNotNull(resultUser.getId());
        assertEquals(TestConstants.USER_3_ID, resultUser.getId());
        assertEquals(TestConstants.USER_3_NAME, resultUser.getEmail());

    }


    @Test
    public void testCreateUser() throws BadParameterException {
        final String username = "george";
        final String email = "george@will.run";
        final String password = "Passw0rd";

        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(email);
        AuthorityEntity testAuthority = new AuthorityEntity();
        testAuthority.setUser(testUser);
        testAuthority.setName(AuthorityName.ROLE_USER);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);

        ArgumentCaptor<UserEntity> dateCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(dateCapture.capture())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(authorityRepository.save(Mockito.any(AuthorityEntity.class))).thenReturn(testAuthority);

        UserEntity result = userService.createUser(username, email, password);
        assertNotNull(result);
        assertNotNull(result.getAuthorities());
        UserEntity datePasswordCheck = dateCapture.getValue();
        assertNotNull(datePasswordCheck);
        assertTrue(DateUtils.isAfterOrEqual(datePasswordCheck.getCreationDate(), new Date()));
        assertTrue(BCrypt.checkpw(password, datePasswordCheck.getPassword()));
        assertNotEquals(datePasswordCheck.getPassword(), password);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateDeviceForUser_Error() {
        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.ofNullable(null));

        userService.createDeviceForUserAndDevice(TestConstants.USER_3_ID, null, "token");

    }

    @Test
    public void testCreateDeviceForUserAndDevice() {
        String token = "abcdefg1234567";

        ClientDeviceInfo deviceInfo = new ClientDeviceInfo();
        deviceInfo.setBuildNumber(buildNumber);
        deviceInfo.setClientVersion(clientVersion);
        deviceInfo.setClientType(clientType);
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setModel(model);
        deviceInfo.setName(name);
        deviceInfo.setOs(ossystem);
        deviceInfo.setOsVersion(osversion);


        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setUsername(TestConstants.USER_3_NAME);
        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findByUsername(TestConstants.USER_3_NAME)).thenReturn(testUser);

        UserDeviceEntity testUserDevice = new UserDeviceEntity();
        testUserDevice.setUserId(TestConstants.USER_3_ID);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(testUserDevice);

        userService.createDeviceForUserAndDevice(TestConstants.USER_3_ID, deviceInfo, token);

        UserDeviceEntity captured = userDeviceCapture.getValue();
        assertNotNull(captured);
        assertEquals(TestConstants.USER_3_ID, captured.getUserId());

    }

    @Test
    public void testSaveTokenForUserAndDevice() {
        String buildNumber = "buildNumber";
        String clientVersion = "clientVersion";
        ClientType clientType = ClientType.Mobile;
        String deviceId = "deviceId";
        String model = "model";
        String name = "name";
        String ossystem = "os";
        String osversion = "osversion";

        String token = "abcdefg1234567";

        ClientDeviceInfo deviceInfo = new ClientDeviceInfo();
        deviceInfo.setBuildNumber(buildNumber);
        deviceInfo.setClientVersion(clientVersion);
        deviceInfo.setClientType(clientType);
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setModel(model);
        deviceInfo.setName(name);
        deviceInfo.setOs(ossystem);
        deviceInfo.setOsVersion(osversion);


        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setUsername(TestConstants.USER_3_NAME);

        UserDeviceEntity testUserDevice = new UserDeviceEntity();
        testUserDevice.setUserId(TestConstants.USER_3_ID);

        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findByUsername(TestConstants.USER_3_NAME)).thenReturn(testUser);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(testUserDevice);

        userService.saveTokenForUserAndDevice(testUser, deviceInfo, token);

        UserDeviceEntity captured = userDeviceCapture.getValue();
        assertNotNull(captured);
        assertEquals(TestConstants.USER_3_ID, captured.getUserId());

    }

    @Test
    public void testUpdateLoginForUser() {
        String token = "abcdefg1234567";

        UserDeviceEntity deviceInfo = new UserDeviceEntity();
        deviceInfo.setBuildNumber(buildNumber);
        deviceInfo.setClientVersion(clientVersion);
        deviceInfo.setClientType(clientType);
        deviceInfo.setClientDeviceId(deviceId);
        deviceInfo.setModel(model);
        deviceInfo.setName(name);
        deviceInfo.setOs(ossystem);
        deviceInfo.setOsVersion(osversion);


        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        Mockito.when(userRepository.findByUsername(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(deviceInfo);

        ArgumentCaptor<UserEntity> userCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCapture.capture())).thenReturn(null);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);


        Date now = new Date();
        Date thirtySecondsAgo = new Date(now.getTime() - 30000);

        userService.updateLoginForUser(TestConstants.USER_3_NAME, token);

        UserDeviceEntity capturedUserDevice = userDeviceCapture.getValue();
        assertNotNull(capturedUserDevice);
        System.out.println(now);
        System.out.println(now.getTime());
        System.out.println(capturedUserDevice.getLastLogin());
        System.out.println(capturedUserDevice.getLastLogin().getTime());
        assertTrue(DateUtils.isAfterOrEqual(thirtySecondsAgo, capturedUserDevice.getLastLogin()));

        UserEntity caturedUser = userCapture.getValue();
        assertNotNull(caturedUser);
        assertTrue(DateUtils.isAfterOrEqual(thirtySecondsAgo, caturedUser.getLastLogin()));


    }

    @Test
    public void testChangePasswordForUser() {
        var userName = TestConstants.USER_1_EMAIL;
        var newPassword = "NEWPASSWORD";
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        long startTime = new Date().getTime();

        // create fixtures
        UserEntity mockUserEntity = new UserEntity();

        Mockito.when(userRepository.findByEmail(userName)).thenReturn(mockUserEntity);
        ArgumentCaptor<UserEntity> userCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCapture.capture())).thenReturn(null);

        // test call
        userService.changePassword(userName, newPassword);

        // verify calls
        Mockito.verify(userRepository, times(1))
                .findByEmail(userName);

        // verify capture
        Assert.assertNotNull("value captured on save", userCapture.getValue());
        Assert.assertEquals("changed password doesn't match", userCapture.getValue().getPassword().substring(0, 4), encodedPassword.substring(0, 4));
        Assert.assertTrue("date of password change should be set", userCapture.getValue().getLastPasswordResetDate().getTime() >= startTime);
    }

    @Test
    public void testRemoveLoginForUser() {
        String token = "abcdefg1234567";

        UserDeviceEntity deviceInfo = new UserDeviceEntity();
        deviceInfo.setBuildNumber(buildNumber);
        deviceInfo.setClientVersion(clientVersion);
        deviceInfo.setClientType(clientType);
        deviceInfo.setClientDeviceId(deviceId);
        deviceInfo.setModel(model);
        deviceInfo.setName(name);
        deviceInfo.setOs(ossystem);
        deviceInfo.setOsVersion(osversion);


        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(deviceInfo);

        userService.removeLoginForUser(TestConstants.USER_3_NAME, token);

        Mockito.verify(userDeviceRepository, times(1)).delete(deviceInfo);

    }

}