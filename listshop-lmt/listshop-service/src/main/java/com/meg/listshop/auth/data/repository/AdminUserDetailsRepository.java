package com.meg.listshop.auth.data.repository;


import com.meg.listshop.auth.data.entity.AdminUserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserDetailsRepository extends JpaRepository<AdminUserDetailsEntity, Long> {


}