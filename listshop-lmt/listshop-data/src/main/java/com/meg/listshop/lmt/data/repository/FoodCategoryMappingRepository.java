package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodCategoryMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodCategoryMappingRepository extends JpaRepository<FoodCategoryMappingEntity, Long>, CustomFoodMappingRepository {

     List<FoodCategoryMappingEntity> findFoodCategoryMappingEntityByTagIdIn( List<Long> tagIds) ;

     FoodCategoryMappingEntity findFoodCategoryMappingEntityByTagId( Long tagId) ;


}