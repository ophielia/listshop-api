package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    @Query(value = "select u.* from users u join user_devices ud using (user_id) where token = CAST(:userDeviceId as text)", nativeQuery = true)
    UserEntity findByToken(@Param("userDeviceId") String userDeviceId);
}