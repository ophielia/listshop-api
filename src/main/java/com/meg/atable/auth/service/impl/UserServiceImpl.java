package com.meg.atable.auth.service.impl;

import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserAccountEntity getUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public UserAccountEntity getUserByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public UserAccountEntity save(UserAccountEntity user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAllInBatch();
    }


}
