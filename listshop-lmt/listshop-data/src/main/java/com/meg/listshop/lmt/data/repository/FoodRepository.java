package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodEntity, Long> {

    List<FoodEntity> findFoodEntitiesByNameContainsIgnoreCaseAndCategoryId( String name, Long categoryId);
    List<FoodEntity> findFoodEntitiesByNameContainsIgnoreCase( String name);
}