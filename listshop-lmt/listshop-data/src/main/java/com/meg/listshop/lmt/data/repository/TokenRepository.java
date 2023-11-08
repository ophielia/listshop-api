package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    List<TokenEntity> findByTokenValue(String tokenValue);


}