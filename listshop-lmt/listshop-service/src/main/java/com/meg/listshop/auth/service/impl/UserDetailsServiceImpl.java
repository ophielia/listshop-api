package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.conversion.service.handlers.AbstractConversionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by stephan on 20.03.16.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        LOG.debug("Entering in loadUserByUsername Method...");
        UserEntity user = userRepository.findByEmail(username);
        if(user == null){
            LOG.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }

        LOG.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }
}
