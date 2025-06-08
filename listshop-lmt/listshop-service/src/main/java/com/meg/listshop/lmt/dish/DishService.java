package com.meg.listshop.lmt.dish;

import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    List<DishEntity> getDishesForUserName(String userId);

    List<DishEntity> getDishesForUser(Long userId);

    DishEntity getDishForUserById(String username, Long dishId);

    DishEntity getDishForUserById(Long userId, Long dishId);

    DishEntity save(DishEntity dish, boolean doAutotag);

    DishEntity createDish(Long userId, DishEntity dish);

    List<DishEntity> save(List<DishEntity> dishes);

    List<DishEntity> getDishes(List<Long> dishIds);

    List<DishEntity> getDishes(String userName, List<Long> dishIds);

    Map<Long, DishEntity> getDictionaryForIdList(List<Long> dishIds);

    void updateLastAddedForDish(Long dishId);

    List<DishEntity> getDishesToAutotag(Long statusFlag, int dishLimit);

    void addIngredientToDish(Long id, Long dishId, DishItemDTO validatedEntry);

    void updateIngredientInDish(Long id, Long dishId, DishItemDTO validatedEntry);

    DishDTO getDishForV2Display(Long userId, Long dishId);

    void deleteIngredientFromDish(Long userId, Long dishId, Long ingredientId);

    List<DishItemDTO> getDishIngredients(Long userId, Long dishId);
}
