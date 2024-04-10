package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodConversionRepository extends JpaRepository<FoodConversionEntity, Long> {


    List<FoodConversionEntity> findAllByConversionId(Long foodId);

    List<FoodConversionEntity> findAllByFoodId(Long foodId);

    List<FoodConversionEntity> findAllByFoodIdIn(List<Long> foodId);
}