/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.data.entity.AdminUserDetailsEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.api.exceptions.UserCreateException;
import com.meg.listshop.lmt.api.exception.BadParameterException;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    UserEntity getUserById(Long userId);

    UserEntity getUserByUserEmail(String userEmail);

    UserEntity save(UserEntity user);

    UserEntity createUser(String decodedEmail, String decodedPassword, boolean createList) throws BadParameterException, UserCreateException;

    UserEntity updateLoginForUser(String username, String token, ClientDeviceInfo deviceInfo);

    void saveTokenForUserAndDevice(UserEntity userEntity, ClientDeviceInfo deviceInfo, String token);

    void createDeviceForUserAndDevice(Long userId, ClientDeviceInfo deviceInfo, String token);

    void removeLoginForUser(String name, String token);

    void changePassword(Long userId, String newPassword);

    void changePassword(String userName, String newPassword, String originalPassword);

    void deleteUser(String name);

    List<UserEntity> findUsersByEmail(String searchEmail);

    UserEntity getUserByListId(Long listId);

    List<UserEntity> getUsersWithTags();

    AdminUserDetailsEntity getAdminUserById(Long userId);
}
