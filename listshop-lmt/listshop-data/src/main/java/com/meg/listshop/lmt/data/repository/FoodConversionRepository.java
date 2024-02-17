package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.FoodConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodConversionRepository extends JpaRepository<FoodConversionEntity, Long> {


}