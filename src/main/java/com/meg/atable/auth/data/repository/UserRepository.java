package com.meg.atable.auth.data.repository;

import com.meg.atable.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);
}