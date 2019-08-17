package com.meg.atable.auth.data.repository;

import com.meg.atable.auth.data.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {


}