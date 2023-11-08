/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.data.entity.*;
import com.meg.listshop.auth.data.repository.AdminUserDetailsRepository;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.AuthenticationException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AdminUserDetailsRepository adminUserRepository;

    private final UserDeviceRepository userDeviceRepository;

    private final AuthorityRepository authorityRepository;

    private final AuthenticationManager authenticationManager;

    protected final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDeviceRepository userDeviceRepository,
                           AuthorityRepository authorityRepository, AuthenticationManager authenticationManager,
                           AdminUserDetailsRepository adminUserDetailsRepository) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.authorityRepository = authorityRepository;
        this.authenticationManager = authenticationManager;
        this.adminUserRepository = adminUserDetailsRepository;
    }

    @Override
    public UserEntity getUserById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null);
    }

    @Override
    public UserEntity getUserByUserEmail(String userName) {

        return userRepository.findByEmail(userName.toLowerCase());
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity createUser(String email, String decodedPassword) throws BadParameterException {
        logger.info(String.format("Creating user: [%s]",email));
        // check if username exists
        UserEntity existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new BadParameterException("User already present for email [" + email + "].");
        }

        // encode password
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(decodedPassword);
        // create new userEntity and fill in
        var newUser = new UserEntity( email, encodedPassword);
        // add creation date and enabled
        newUser.setCreationDate(new Date());
        newUser.setEnabled(true);
        // save user
        var createdUser = userRepository.save(newUser);
        // create authorities
        var authority = createUserAuthorityForUser(createdUser);
        createdUser.getAuthorities().add(authority);
        // save authorities and return
        logger.debug(String.format("Finished creating new user[%s]",createdUser));
        return userRepository.save(createdUser);
    }

    @Override
    public UserEntity updateLoginForUser(String username, String token, ClientDeviceInfo deviceInfo) {
        logger.debug(String.format("Begin updateLoginForUser: username[%s], token[%s], device[%s]",username, token,deviceInfo));
        // create last login date
        var lastLogin = new Date();

        // get user entity
        var userEntity = userRepository.findByEmail(username);
        // get user_device
        var userDeviceEntity = userDeviceRepository.findByToken(token);

        if (userEntity == null || userDeviceEntity == null) {
            throw new AuthenticationException("user or user device is null.");
        }
        //  update last_login
        userDeviceEntity.setLastLogin(lastLogin);
        if (deviceInfo != null) {
            userDeviceEntity.setClientVersion(deviceInfo.getClientVersion());
            userDeviceEntity.setBuildNumber(deviceInfo.getBuildNumber());
        }
        userDeviceRepository.save(userDeviceEntity);
        userEntity.setLastLogin(lastLogin);
        logger.debug(String.format("Success - updateLoginForUser complete for username[%s]",username));
        return userRepository.save(userEntity);

    }

    @Override
    public void saveTokenForUserAndDevice(UserEntity userEntity, ClientDeviceInfo deviceInfo, String token) {
        // get user id
        Long userId = userEntity.getId();

        // create device for user
        createDeviceForUserAndDevice(userId, deviceInfo, token);

        // update last login time
        updateLoginForUser(userEntity.getUsername(), token, deviceInfo );

    }

    @Override
    public void createDeviceForUserAndDevice(Long userId, ClientDeviceInfo deviceInfo, String token) {
        // get user
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) {
            throw new ObjectNotFoundException("Can't retrieve user for userId [" + userId + "]");
        }

        // create device info
        var userDeviceEntity = new UserDeviceEntity();
        userDeviceEntity.setUserId(userId);
        userDeviceEntity.setToken(token);
        userDeviceEntity.setBuildNumber(deviceInfo.getBuildNumber());
        userDeviceEntity.setClientVersion(deviceInfo.getClientVersion());
        userDeviceEntity.setClientType(deviceInfo.getClientType());
        userDeviceEntity.setClientDeviceId(deviceInfo.getDeviceId());
        userDeviceEntity.setModel(deviceInfo.getModel());
        userDeviceEntity.setName(deviceInfo.getName());
        userDeviceEntity.setOs(deviceInfo.getOs());
        userDeviceEntity.setOsVersion(deviceInfo.getOsVersion());


        // save device info
        userDeviceRepository.save(userDeviceEntity);


    }

    @Override
    public void removeLoginForUser(String name, String token) {
        logger.debug(String.format("Begin removeLoginForUser: user[%s]", name));
        // get user_device
        var userDeviceEntity = userDeviceRepository.findByToken(token);
        Long userId = userDeviceEntity.getUserId();

        if (userDeviceEntity == null) {
            logger.warn(String.format("Nothing to logout. No device found for token: token[%s]", token));
            return;
        }
        //  update last_login
        userDeviceRepository.delete(userDeviceEntity);
        logger.info(String.format("User[%s] successfully logged out.", userId));
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        // get user
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
            logger.warn(String.format("User not found in change password userId[%s]", userId));
            throw new ObjectNotFoundException(String.format("User [%s] not found for password change.", userId));
        }
        var user = userEntity.get();

        changePassword(user, newPassword);
    }

    @Override
    public void changePassword(String email, String newPassword, String originalPassword) {
        logger.debug(String.format("Begin changePassword, user[%s]", email));
        // get user
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn(String.format("no user found for email [%s] in changePassword", email));
            throw new ObjectNotFoundException(String.format("User [%s] not found for password change.", email));
        }

        // test original password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            originalPassword
                    )
            );
        } catch (BadCredentialsException badCredentialsException) {
            // catch and rethrow, logging along the way
            logger.warn(String.format("ChangingPassword - exception logging in with original password, user[%s]", user.getId()));
            throw badCredentialsException;
        }

        changePassword(user, newPassword);
        logger.info(String.format("Successfully changed password for user[%s]", user.getId()));
    }

    @Override
    public void deleteUser(String email) {
        logger.debug(String.format("Request received to delete user [%s]", email));
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn(String.format("no user found for email [%s] in deleteUser", email));
            throw new ObjectNotFoundException(String.format("No user found for email: %s", email));
        }
        logger.info(String.format("Will delete user [%s]", user));
        userRepository.deleteUser(user.getId());
        userRepository.flush();
    }

    @Override
    public List<UserEntity> findUsersByEmail(String searchEmail) {
        return userRepository.findByEmailContainingIgnoreCase(searchEmail.toLowerCase());
    }

    @Override
    public UserEntity getUserByListId(Long listId) {
        return userRepository.findByListId(listId);
    }

    @Override
    public AdminUserDetailsEntity getAdminUserById(Long userId) {
        Optional<AdminUserDetailsEntity> userOpt = adminUserRepository.findById(userId);
        return userOpt.orElse(null);
    }

    private void changePassword(UserEntity user, String password) {
        // encode password
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);
        user.setLastPasswordResetDate(new Date());
        userRepository.save(user);
        logger.info(String.format("Password successfully changed for userId[%s]", user.getId()));
    }

    private AuthorityEntity createUserAuthorityForUser(UserEntity createdUser) {
        var authority = new AuthorityEntity();
        authority.setName(AuthorityName.ROLE_USER);
        authority.setUser(createdUser);

        return authorityRepository.save(authority);
    }



}
