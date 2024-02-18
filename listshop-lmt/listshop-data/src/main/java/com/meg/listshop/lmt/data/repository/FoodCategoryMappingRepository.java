package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodCategoryMappingEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FoodCategoryMappingRepository extends JpaRepository<FoodCategoryMappingEntity, Long> {

     //@Query(value = "select * from food_category_mapping where tag_id in (:tagIds)", nativeQuery = true)
     //List<FoodCategoryMappingEntity> findFoodCategoryMappingEntityByTagIdIn( List<Long> tagIds) ;
     List<FoodCategoryMappingEntity> findFoodCategoryMappingEntityByTagIdIn( List<Long> tagIds) ;

     FoodCategoryMappingEntity findFoodCategoryMappingEntityByTagId(Long tagId);
}