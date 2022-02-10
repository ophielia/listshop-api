/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.AuthenticationException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserDeviceRepository userDeviceRepository;

    private final AuthorityRepository authorityRepository;

    protected final Log logger = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDeviceRepository userDeviceRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserEntity getUserById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null);
    }

    @Override
    public UserEntity getUserByUserEmail(String userName) {

        return userRepository.findByEmail(userName);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity createUser(String username, String email, String decodedPassword) throws BadParameterException {
        // check if username exists
        UserEntity existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new BadParameterException("User already present for email [" + email + "].");
        }

        // encode password
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(decodedPassword);
        // create new userentity and fill in
        var newUser = new UserEntity(username, email, encodedPassword);
        // add creation date
        newUser.setCreationDate(new Date());
        // save user
        var createdUser = userRepository.save(newUser);
        // create authorities
        var authority = createUserAuthorityForUser(createdUser);
        createdUser.getAuthorities().add(authority);
        // save authorities and return
        return userRepository.save(createdUser);
    }

    @Override
    public UserEntity updateLoginForUser(String username, String token) {
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
        userDeviceRepository.save(userDeviceEntity);
        userEntity.setLastLogin(lastLogin);
        return userRepository.save(userEntity);

    }

    @Override
    public void saveTokenForUserAndDevice(UserEntity userEntity, ClientDeviceInfo deviceInfo, String token) {
        // get user id
        Long userId = userEntity.getId();

        // create device for user
        createDeviceForUserAndDevice(userId, deviceInfo, token);

        // update last login time
        updateLoginForUser(userEntity.getUsername(), token);

    }

    @Override
    public void createDeviceForUserAndDevice(Long userId, ClientDeviceInfo deviceInfo, String token) {
        // get user
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (!userEntityOptional.isPresent()) {
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
        // get user_device
        var userDeviceEntity = userDeviceRepository.findByToken(token);

        if (userDeviceEntity == null) {
            throw new AuthenticationException("no user device found to logout.");
        }
        //  update last_login
        userDeviceRepository.delete(userDeviceEntity);
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        // get user
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (!userEntity.isPresent()) {
            throw new ObjectNotFoundException(String.format("User [%s] not found for password change.", userId));
        }
        var user = userEntity.get();

        changePassword(user, newPassword);
    }

    @Override
    public void changePassword(String eMail, String newPassword) {
        // get user
        UserEntity user = userRepository.findByEmail(eMail);
        if (user == null) {
            logger.warn(String.format("no user found for email [%s] in changePassword", eMail));
            throw new ObjectNotFoundException(String.format("User [%s] not found for password change.", eMail));
        }

        changePassword(user, newPassword);


    }

    @Override
    public void deleteUser(String email) {
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn(String.format("no user found for email [%s] in deleteUser", email));
            throw new ObjectNotFoundException(String.format("No user found for email: %s", email));
        }
        userRepository.deleteUser(user.getId());
        userRepository.flush();
    }

    private void changePassword(UserEntity user, String password) {
        // encode password
        var encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);
        user.setLastPasswordResetDate(new Date());
        userRepository.save(user);
    }

    private AuthorityEntity createUserAuthorityForUser(UserEntity createdUser) {
        var authority = new AuthorityEntity();
        authority.setName(AuthorityName.ROLE_USER);
        authority.setUser(createdUser);

        return authorityRepository.save(authority);
    }



}
