package com.meg.atable.auth.service.impl;

import com.meg.atable.auth.data.entity.AuthorityEntity;
import com.meg.atable.auth.data.entity.AuthorityName;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.data.repository.AuthorityRepository;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public UserEntity getUserById(Long userId) {
        Optional<UserEntity> userOpt =  userRepository.findById(userId);
        return userOpt.orElse(null);
    }

    @Override
    public UserEntity getUserByUserEmail(String userName) {
        return userRepository.findByEmail(userName);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity createUser(String username, String email, String decodedPassword) {
        // encode password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(decodedPassword);
        // create new userentity and fill in
        UserEntity newUser = new UserEntity(username, email, encodedPassword);
        // add creation date
        newUser.setCreationDate(new Date());
        // save user
        UserEntity createdUser = userRepository.save(newUser);
        // create authorities
        AuthorityEntity authority = createUserAuthorityForUser(createdUser);
        createdUser.getAuthorities().add(authority);
        // save authorities and return
        return userRepository.save(createdUser);
    }

    private AuthorityEntity createUserAuthorityForUser(UserEntity createdUser) {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setName(AuthorityName.ROLE_USER);
        authority.setUser(createdUser);

        return authorityRepository.save(authority);
    }



}
