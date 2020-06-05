package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {


    UserDeviceEntity findByToken(String token);
}