/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.auth.data.repository;

import com.meg.listshop.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<UserEntity, Long>, CustomUserRepository {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    UserEntity findByEmailIgnoreCase(String email);

    @Query(value = """
            select u.* from users u
            where exists (
                select 1 from user_devices ud
                where ud.user_id = u.user_id and ud.token = :token
            )
            """, nativeQuery = true)
    UserEntity findByToken(@Param("token") String token);

    @Query(value = "select u.* from users u join list l using (user_id) where l.list_id =:listId", nativeQuery = true)
    UserEntity findByListId(@Param("listId") Long listId);

    List<UserEntity> findByEmailContainingIgnoreCase(String toLowerCase);

    @Query(value = "select distinct u.* from users u join tag t using (user_id)", nativeQuery = true)
    List<UserEntity> findUsersWithTags();
}
