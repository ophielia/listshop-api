package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodEntity;
import com.meg.listshop.lmt.data.entity.FoodEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodEntryRepository extends JpaRepository<FoodEntryEntity, Long> {

    @Query("select distinct f from FoodEntryEntity f where lower(f.name) like lower(?1)")
    List<FoodEntryEntity> findFoodMatches(String name);
}