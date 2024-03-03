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

    @Query(value = "select u.* from users u join user_devices ud using (user_id) where token = CAST(:userDeviceId as text)", nativeQuery = true)
    UserEntity findByToken(@Param("userDeviceId") String userDeviceId);

    @Query(value = "select u.* from users u join list l using (user_id) where l.list_id =:listId", nativeQuery = true)
    UserEntity findByListId(@Param("listId") Long listId);

    List<UserEntity> findByEmailContainingIgnoreCase(String toLowerCase);

    @Query(value = "select distinct u.* from users u join tag t using (user_id)", nativeQuery = true)
    List<UserEntity> findUsersWithTags();
}