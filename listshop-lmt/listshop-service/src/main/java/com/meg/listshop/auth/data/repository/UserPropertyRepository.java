package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserPropertyRepository extends JpaRepository<UserPropertyEntity, Long> {


    List<UserPropertyEntity> findByUserId(Long userId);

    Optional<UserPropertyEntity> findByUserIdAndKey(Long userId, String propertyKey);
}