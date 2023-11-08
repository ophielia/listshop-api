package com.meg.listshop.auth.service;

import com.meg.listshop.auth.data.entity.UserPropertyEntity;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserPropertyChangeListener {

    void onPropertyUpdate(List<UserPropertyEntity> savedProperties);

}
