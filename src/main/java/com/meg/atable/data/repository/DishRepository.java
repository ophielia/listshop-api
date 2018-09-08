package com.meg.atable.data.repository;

import com.meg.atable.data.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByUserId(Long userid);

}