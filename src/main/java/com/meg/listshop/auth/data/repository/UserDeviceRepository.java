package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {


    UserDeviceEntity findByToken(String token);

    List<UserDeviceEntity> findByUserIdAndClientTypeAndName(Long userId, ClientType clientType, String name);
}