package com.meg.atable.data.repository;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByUserAccount(UserAccountEntity user);

    Collection<DishEntity> findByUserAccountUsername(String userName);
}