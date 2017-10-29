package com.meg.atable.service;

import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.data.entity.DishEntity;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    Collection<DishEntity> getDishesForUserName(String userId) throws UserNotFoundException;

    Optional<DishEntity> getDishById(Long dishId);

    Optional<DishEntity> getDishForUserById(String username, Long dishId);

    DishEntity save(DishEntity dish);

}
