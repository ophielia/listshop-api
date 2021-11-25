package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.data.entity.AuthorityEntity;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.AuthorityRepository;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.AuthenticationException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserDeviceRepository userDeviceRepository;

    private final AuthorityRepository authorityRepository;

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
    public UserEntity createUser(String username, String email, String decodedPassword) throws BadParameterException {
        // check if username exists
        UserEntity existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new BadParameterException("User already present for email [" + email + "].");
        }

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

        // update last login time
        updateLoginForUser(userEntity.getUsername(), token);

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


    }

    private void deleteExistingEntry(Long userId, ClientType clientType, String name) {
        List<UserDeviceEntity> existing = userDeviceRepository.findByUserIdAndClientTypeAndName(userId, clientType, name);
        if (!existing.isEmpty()) {
            for (UserDeviceEntity entry : existing) {
                userDeviceRepository.delete(entry);
            }
        }
    }

    @Override
    public void removeLoginForUser(String name, String token) {
        // get user_device
        UserDeviceEntity userDeviceEntity = userDeviceRepository.findByToken(token);

        if (userDeviceEntity == null) {
            throw new AuthenticationException("no user device found to logout.");
        }
        //  update last_login
        userDeviceRepository.delete(userDeviceEntity);
    }

    private AuthorityEntity createUserAuthorityForUser(UserEntity createdUser) {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setName(AuthorityName.ROLE_USER);
        authority.setUser(createdUser);

        return authorityRepository.save(authority);
    }



}
