package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.AuthenticationException;
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

    private UserRepository userRepository;

    private UserDeviceRepository userDeviceRepository;

    private AuthorityRepository authorityRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDeviceRepository userDeviceRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserEntity getUserById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
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

    @Override
    public UserEntity updateLoginForUser(String username, String token) {
        // create last login date
        Date lastLogin = new Date();

        // get user entity
        UserEntity userEntity = userRepository.findByUsername(username);
        // get user_device
        UserDeviceEntity userDeviceEntity = userDeviceRepository.findByToken(token);

        if (userEntity == null || userDeviceEntity == null) {
            throw new AuthenticationException("user or user device is null.");
        }
        //  update last_login
        userDeviceEntity.setLastLogin(lastLogin);
        userDeviceRepository.save(userDeviceEntity);
        userEntity.setLastLogin(lastLogin);
        return userRepository.save(userEntity);

    }

    @Override
    public void saveTokenForUserAndDevice(UserEntity userEntity, ClientDeviceInfo deviceInfo, String token) {
        // get user id
        Long userId = userEntity.getId();

        // create device for user
        createDeviceForUserAndDevice(userId, deviceInfo, token);

        return;
    }

    @Override
    public void createDeviceForUserAndDevice(Long userId, ClientDeviceInfo deviceInfo, String token) {
        // get user
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (!userEntityOptional.isPresent()) {
            throw new RuntimeException("Can't retrieve user for userId [" + userId + "]");
        }
        // create device info
        UserDeviceEntity userDeviceEntity = new UserDeviceEntity();
        userDeviceEntity.setUserId(userId);
        userDeviceEntity.setToken(token);
        userDeviceEntity.setBuildNumber(deviceInfo.getBuildNumber());
        userDeviceEntity.setClientVersion(deviceInfo.getClientVersion());
        userDeviceEntity.setClientType(deviceInfo.getClientType());
        userDeviceEntity.setClientDeviceId(deviceInfo.getDeviceId());
        userDeviceEntity.setModel(deviceInfo.getModel());
        userDeviceEntity.setName(deviceInfo.getName());
        userDeviceEntity.setOs(deviceInfo.getOs());
        userDeviceEntity.setOsVersion(deviceInfo.getOsVersion());


        // save device info
        userDeviceRepository.save(userDeviceEntity);

        return;
    }

    private AuthorityEntity createUserAuthorityForUser(UserEntity createdUser) {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setName(AuthorityName.ROLE_USER);
        authority.setUser(createdUser);

        return authorityRepository.save(authority);
    }



}
