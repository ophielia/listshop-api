package com.meg.atable.service;

import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    Collection<DishEntity> getDishesForUserName(String userId) throws UserNotFoundException;

    Optional<DishEntity> getDishById(Long dishId);

    Optional<DishEntity> getDishForUserById(String username, Long dishId);

    DishEntity save(DishEntity dish, boolean doAutotag);
    List<DishEntity> save(List<DishEntity> dishes);

    List<DishEntity> getDishes(List<Long> dishIds);

    Map<Long,DishEntity> getDictionaryForIdList(List<Long> dishIds);

    List<TagEntity> getDishesForTagChildren(Long tagId, String name);

    void updateLastAddedForDish(Long dishId);
}
