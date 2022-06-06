/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.service;

import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.lmt.api.exception.BadParameterException;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserPropertyService {

    List<UserPropertyEntity> getPropertiesForUser(String userName);

    UserPropertyEntity getPropertyForUser(String userName, String propertyKey) throws BadParameterException;

    void setPropertiesForUser(String userName, List<UserPropertyEntity> userPropertyEntities) throws BadParameterException;

}
