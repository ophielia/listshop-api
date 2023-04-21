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
import com.meg.listshop.auth.service.UserPropertyChangeListener;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserPropertyServiceImpl implements UserPropertyService {

    private final UserService userService;

    private final UserPropertyRepository userPropertyRepository;

    private final List<UserPropertyChangeListener> listeners = new CopyOnWriteArrayList<>();

    protected final Logger logger = LoggerFactory.getLogger(UserPropertyServiceImpl.class);

    @Autowired
    public UserPropertyServiceImpl(UserService userService, UserPropertyRepository userPropertyRepository) {
        this.userService = userService;
        this.userPropertyRepository = userPropertyRepository;
    }

    @Override
    public void addUserPropertyChangeListener(UserPropertyChangeListener propertyChangeListener) {
        listeners.add(propertyChangeListener);
    }

    @Override
    public List<UserPropertyEntity> getPropertiesForUser(String userName) throws BadParameterException {
        UserEntity user = getUserForUserName(userName);
        return userPropertyRepository.findByUserId(user.getId());
    }

    @Override
    public UserPropertyEntity getPropertyForUser(String userName, String propertyKey) throws BadParameterException {
        if (propertyKey == null) {
            throw new BadParameterException("Property Key is null in getPropertyForUser");
        }
        UserEntity user = getUserForUserName(userName);

        return userPropertyRepository.findByUserIdAndKey(user.getId(), propertyKey).orElse(null);
    }

    @Override
    public void setPropertiesForUser(String userName, List<UserPropertyEntity> userPropertyEntities) throws BadParameterException {
        setPropertiesForUser(userName, userPropertyEntities, false);
    }

    @Override
    public void setPropertiesForUser(String userName, List<UserPropertyEntity> userPropertyEntities, boolean suppressNotifications) throws BadParameterException {
        if (userPropertyEntities == null) {
            throw new BadParameterException("We can take an empty list  - but not null. Check userPropertyEntities");
        }
        UserEntity user = getUserForUserName(userName);

        List<UserPropertyEntity> databaseProperties = userPropertyRepository.findByUserId(user.getId());
        Map<String, UserPropertyEntity> existingProperties = databaseProperties.stream()
                .collect(Collectors.toMap(UserPropertyEntity::getKey, Function.identity()));

        List<UserPropertyEntity> toSaveList = new ArrayList<>();
        List<UserPropertyEntity> toDeleteList = new ArrayList<>();
        for (UserPropertyEntity toSave : userPropertyEntities) {
            // get any existing entry for this property / user
            UserPropertyEntity dbProperty = existingProperties.getOrDefault(toSave.getKey(), null);
            if (toSave.getValue() == null && dbProperty != null) {
                toDeleteList.add(dbProperty);
            } else if (toSave.getValue() != null) {
                // if none exists, created one
                if (dbProperty == null) {
                    dbProperty = new UserPropertyEntity();
                    dbProperty.setUser(user);
                    dbProperty.setKey(toSave.getKey());
                }

                // set value
                dbProperty.setValue(toSave.getValue());
                // save or update
                toSaveList.add(dbProperty);
            }
        }
        if (!toSaveList.isEmpty()) {
            userPropertyRepository.saveAll(toSaveList);
            if (!suppressNotifications) {
                firePropertiesUpdatedEvent(toSaveList);
            }
        }
        if (!toDeleteList.isEmpty()) {
            userPropertyRepository.deleteAll(toDeleteList);
        }

    }

    private void firePropertiesUpdatedEvent(List<UserPropertyEntity> updatedProperties) {
        for (UserPropertyChangeListener listener : listeners) {
            listener.onPropertyUpdate(updatedProperties);
        }
    }


    private UserEntity getUserForUserName(String userName) throws UserNotFoundException, BadParameterException {
        if (userName == null) {
            throw new BadParameterException(userName);
        }
        UserEntity user = userService.getUserByUserEmail(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return user;
    }
}
