package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.pojos.DishItemDTO;

import java.util.List;

public interface CustomDishItemRepository {

    List<DishItemDTO> getIngredientsForDish(Long dishId);
}