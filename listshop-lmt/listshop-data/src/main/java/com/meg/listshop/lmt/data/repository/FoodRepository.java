package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodEntity, Long> {

    @Query("select f from FoodEntity f where lower(f.name) like lower(?1) and f.hasFactor= true")
    List<FoodEntity> findFoodMatches(String name);
}