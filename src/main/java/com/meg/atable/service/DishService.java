package com.meg.atable.service;

import com.meg.atable.model.Dish;
import com.meg.atable.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    public List<Dish> getDishesForUserId(Long userId);

    Collection<Dish> getDishesForUserName(String userId);

    Optional<Dish> getDishById(Long dishId);

    Dish save(Dish dish);

    void deleteAll();


}
