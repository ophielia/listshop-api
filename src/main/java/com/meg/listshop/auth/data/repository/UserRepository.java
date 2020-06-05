package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    @Query(value = "select u.* from users u join user_devices ud using (user_id) where token = ?", nativeQuery = true)
    UserEntity findByToken(String userDeviceId);
}