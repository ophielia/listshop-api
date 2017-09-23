package com.meg.atable.auth.data.repository;

import com.meg.atable.auth.data.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserAccountEntity, Long> {

    UserAccountEntity findByUsername(String username);
}