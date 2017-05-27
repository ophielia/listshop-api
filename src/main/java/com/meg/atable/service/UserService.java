package com.meg.atable.service;

import com.meg.atable.model.User;

import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    public User getUserById(Long userId);

    public Optional<User> getUserByUserName(String userName);

    public User save(User user);

    void deleteAll();
}
