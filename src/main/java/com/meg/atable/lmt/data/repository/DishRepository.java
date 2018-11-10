package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByUserId(Long userid);

}