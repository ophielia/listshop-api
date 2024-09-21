package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

 @Query("select a FROM AuthorityEntity a where a.user.id = ?1")
    List<AuthorityEntity> findByUserId(Long userId);

}