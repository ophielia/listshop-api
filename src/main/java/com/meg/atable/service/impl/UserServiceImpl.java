package com.meg.atable.service.impl;

import com.meg.atable.model.UserAccount;
import com.meg.atable.repository.UserRepository;
import com.meg.atable.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserAccount getUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public Optional<UserAccount> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public UserAccount save(UserAccount user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAllInBatch();
    }
}
