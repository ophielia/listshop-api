package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodCategoryMappingEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;

import java.util.List;
import java.util.Set;

public interface CustomFoodMappingRepository {

    List<FoodMappingDTO> retrieveAllFoodMappingDTOs();
}