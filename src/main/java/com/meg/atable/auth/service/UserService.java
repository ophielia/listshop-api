package com.meg.atable.auth.service;

import com.meg.atable.auth.data.entity.UserAccountEntity;

import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    public UserAccountEntity getUserById(Long userId);

    public UserAccountEntity getUserByUserName(String userName);

    public UserAccountEntity save(UserAccountEntity user);

    void deleteAll();
}
