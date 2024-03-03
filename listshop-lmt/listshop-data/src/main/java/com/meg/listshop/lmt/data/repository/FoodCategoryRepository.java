package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategoryEntity, Long> {


}