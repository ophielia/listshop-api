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
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserPropertyServiceImpl implements UserPropertyService {

    private final UserService userService;

    private final UserPropertyRepository userPropertyRepository;

    protected final Log logger = LogFactory.getLog(UserPropertyServiceImpl.class);

    @Autowired
    public UserPropertyServiceImpl(UserService userService, UserPropertyRepository userPropertyRepository) {
        this.userService = userService;
        this.userPropertyRepository = userPropertyRepository;
    }


    @Override
    public List<UserPropertyEntity> getPropertiesForUser(String userName) {
        UserEntity user = getUserForUserName(userName);
        return userPropertyRepository.findByUserId(user.getId());
    }

    @Override
    public UserPropertyEntity getPropertyForUser(String userName, String propertyKey) throws BadParameterException {
        if (propertyKey == null) {
            throw new BadParameterException("Property Key is null in getPropertyForUser");
        }
        UserEntity user = getUserForUserName(userName);

        return userPropertyRepository.findByUserIdAndPropertyKey(user.getId(), propertyKey).orElse(null);
    }

    @Override
    public void setPropertiesForUser(String userName, List<UserPropertyEntity> userPropertyEntities) throws BadParameterException {
        if (userPropertyEntities == null) {
            throw new BadParameterException("We can take an empty list  - but not null. Check userPropertyEntities");
        }
        UserEntity user = getUserForUserName(userName);

        for (UserPropertyEntity toSave : userPropertyEntities) {
            if (toSave.getKey() == null) {
                continue;
            }
            // get any existing entry for this property / user
            Optional<UserPropertyEntity> dbPropertyOpt = userPropertyRepository.findByUserIdAndPropertyKey(user.getId(), toSave.getKey());
            UserPropertyEntity dbProperty = dbPropertyOpt.orElse(null);
            // if none exists, created one
            if (!dbPropertyOpt.isPresent()) {
                dbProperty = new UserPropertyEntity();
                dbProperty.setUser(user);
                dbProperty.setKey(toSave.getKey());
            }
            // set value
            dbProperty.setValue(toSave.getValue());
            // save or update
            userPropertyRepository.save(dbProperty);
        }
    }

    private UserEntity getUserForUserName(String userName) throws UserNotFoundException {
        if (userName == null) {
            return null;
        }
        UserEntity user = userService.getUserByUserEmail(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return user;
    }
}
