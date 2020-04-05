package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {


}