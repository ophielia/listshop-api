package com.meg.atable.service;

import com.meg.atable.model.UserAccount;

import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    public UserAccount getUserById(Long userId);

    public Optional<UserAccount> getUserByUserName(String userName);

    public UserAccount save(UserAccount user);

    void deleteAll();
}
