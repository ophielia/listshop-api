package com.meg.atable.service;

import com.meg.atable.data.entity.DishEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    public List<DishEntity> getDishesForUserId(Long userId);

    Collection<DishEntity> getDishesForUserName(String userId);

    Optional<DishEntity> getDishById(Long dishId);

    DishEntity save(DishEntity dish);

    void deleteAll();


}
