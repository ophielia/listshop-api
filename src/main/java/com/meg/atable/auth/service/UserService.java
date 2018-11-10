package com.meg.atable.auth.service;

import com.meg.atable.auth.data.entity.UserAccountEntity;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    UserAccountEntity getUserById(Long userId);

    UserAccountEntity getUserByUserName(String userName);

    UserAccountEntity save(UserAccountEntity user);

}
