package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Created by stephan on 20.03.16.
 */
@Service
public class JwtUserDetailsServiceImpl implements ListShopUserDetailsService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    @Autowired
    public JwtUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info(String.format("loading user for email [%s]", email));
        // load by email rather than usernae
        UserEntity user = userRepository.findByEmail(email);

        return createUserDetailsFromUser(user);
    }

    @Override
    public UserDetails loadUserByToken(String token) {

        // load by token
        logger.debug(String.format("loading user for token [%s]", token));
        UserEntity user = userRepository.findByToken(token);

        return createUserDetailsFromUser(user);
    }

    private UserDetails createUserDetailsFromUser(UserEntity user) {
        if (user != null) {
            return JwtUserFactory.create(user);
        }
        return null;
    }
}
