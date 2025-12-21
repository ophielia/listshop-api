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
import com.meg.listshop.auth.data.repository.AdminUserDetailsRepository;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.DateUtils;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImplMockTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDeviceRepository userDeviceRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AdminUserDetailsRepository adminUserDetailsRepository;

    private final String buildNumber = "buildNumber";
    private final String clientVersion = "clientVersion";
    private final ClientType clientType = ClientType.Mobile;
    private final String deviceId = "deviceId";
    private final String model = "model";
    private final String name = "name";
    private final String ossystem = "os";
    private final String osversion = "osversion";

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userDeviceRepository, authorityRepository,
                authenticationManager, adminUserDetailsRepository);
    }

    @Test
    void testGetUserById() {
        UserEntity testUser = new UserEntity();
        testUser.setId(TestConstants.USER_3_ID);
        testUser.setEmail(TestConstants.USER_3_NAME);
        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.of(testUser));

        UserEntity resultUser = userService.getUserById(TestConstants.USER_3_ID);
        Assertions.assertNotNull(resultUser);
        Assertions.assertNotNull(resultUser.getId());
        Assertions.assertEquals(TestConstants.USER_3_ID, resultUser.getId());
        Assertions.assertEquals(TestConstants.USER_3_NAME, resultUser.getEmail());

    }


    @Test
    void testCreateUser() throws BadParameterException {
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

        UserEntity result = userService.createUser(email, password);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getAuthorities());
        UserEntity datePasswordCheck = dateCapture.getValue();
        Assertions.assertNotNull(datePasswordCheck);
        Assertions.assertTrue(DateUtils.isAfterOrEqual(datePasswordCheck.getCreationDate(), new Date()));
        Assertions.assertTrue(BCrypt.checkpw(password, datePasswordCheck.getPassword()));
        Assertions.assertNotEquals(datePasswordCheck.getPassword(), password);

    }

    @Test
    public void testCreateDeviceForUser_Error() {
        Mockito.when(userRepository.findById(TestConstants.USER_3_ID)).thenReturn(Optional.ofNullable(null));


        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.createDeviceForUserAndDevice(TestConstants.USER_3_ID, null, "token");
        });


    }

    @Test
    void testCreateDeviceForUserAndDevice() {
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
        Mockito.when(userRepository.findByEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);

        UserDeviceEntity testUserDevice = new UserDeviceEntity();
        testUserDevice.setUserId(TestConstants.USER_3_ID);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(testUserDevice);

        userService.createDeviceForUserAndDevice(TestConstants.USER_3_ID, deviceInfo, token);

        UserDeviceEntity captured = userDeviceCapture.getValue();
        Assertions.assertNotNull(captured);
        Assertions.assertEquals(TestConstants.USER_3_ID, captured.getUserId());

    }

    @Test
    void testSaveTokenForUserAndDevice() {
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
        Mockito.when(userRepository.findByEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(testUserDevice);

        userService.saveTokenForUserAndDevice(testUser, deviceInfo, token);

        UserDeviceEntity captured = userDeviceCapture.getValue();
        Assertions.assertNotNull(captured);
        Assertions.assertEquals(TestConstants.USER_3_ID, captured.getUserId());

    }

    @Test
    void testUpdateLoginForUser() {
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
        Mockito.when(userRepository.findByEmail(TestConstants.USER_3_NAME)).thenReturn(testUser);
        Mockito.when(userDeviceRepository.findByToken(token)).thenReturn(deviceInfo);

        ArgumentCaptor<UserEntity> userCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCapture.capture())).thenReturn(null);

        ArgumentCaptor<UserDeviceEntity> userDeviceCapture = ArgumentCaptor.forClass(UserDeviceEntity.class);
        Mockito.when(userDeviceRepository.save(userDeviceCapture.capture())).thenReturn(null);


        Date now = new Date();
        Date thirtySecondsAgo = new Date(now.getTime() - 30000);

        userService.updateLoginForUser(TestConstants.USER_3_NAME, token, null );

        UserDeviceEntity capturedUserDevice = userDeviceCapture.getValue();
        Assertions.assertNotNull(capturedUserDevice);
        System.out.println(now);
        System.out.println(now.getTime());
        System.out.println(capturedUserDevice.getLastLogin());
        System.out.println(capturedUserDevice.getLastLogin().getTime());
        Assertions.assertTrue(DateUtils.isAfterOrEqual(thirtySecondsAgo, capturedUserDevice.getLastLogin()));

        UserEntity caturedUser = userCapture.getValue();
        Assertions.assertNotNull(caturedUser);
        Assertions.assertTrue(DateUtils.isAfterOrEqual(thirtySecondsAgo, caturedUser.getLastLogin()));


    }

    @Test
    void testChangePasswordForUser() {
        var userName = TestConstants.USER_1_EMAIL;
        var newPassword = "NEWPASSWORD";
        var originalPassword = "ORIGINALPASSWORD";
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        long startTime = new Date().getTime();

        // create fixtures
        UserEntity mockUserEntity = new UserEntity();

        Mockito.when(userRepository.findByEmail(userName)).thenReturn(mockUserEntity);
        ArgumentCaptor<UserEntity> userCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCapture.capture())).thenReturn(null);

        // test call
        userService.changePassword(userName, newPassword, originalPassword );

        // verify calls
        Mockito.verify(userRepository, times(1))
                .findByEmail(userName);

        // verify capture
        Assertions.assertNotNull(userCapture.getValue(), "value captured on save");
        Assertions.assertEquals(userCapture.getValue().getPassword().substring(0, 4), encodedPassword.substring(0, 4), "changed password doesn't match");
        Assertions.assertTrue(userCapture.getValue().getLastPasswordResetDate().getTime() >= startTime, "date of password change should be set");
    }

    @Test
    public void testChangePasswordForUser_KO() {
        var userName = TestConstants.USER_1_EMAIL;
        var newPassword = "NEWPASSWORD";
        var originalPassword = "ORIGINALPASSWORD";
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        long startTime = new Date().getTime();

        // create fixtures
        UserEntity mockUserEntity = new UserEntity();

        Mockito.when(userRepository.findByEmail(userName)).thenReturn(mockUserEntity);
        ArgumentCaptor<UserEntity> userCapture = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCapture.capture())).thenReturn(null);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);


        Assertions.assertThrows(BadCredentialsException.class, () -> {
            userService.changePassword(userName, newPassword, originalPassword );
        });
        // test call


    }

    @Test
    void testRemoveLoginForUser() {
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
