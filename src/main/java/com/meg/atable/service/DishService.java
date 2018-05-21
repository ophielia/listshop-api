package com.meg.atable.service;

import com.meg.atable.api.exception.ObjectNotFoundException;
import com.meg.atable.api.exception.ObjectNotYoursException;
import com.meg.atable.api.exception.UserNotFoundException;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    Collection<DishEntity> getDishesForUserName(String userId) throws UserNotFoundException;

    DishEntity getDishForUserById(String username, Long dishId) throws ObjectNotFoundException, ObjectNotYoursException;

    DishEntity save(DishEntity dish, boolean doAutotag);
    List<DishEntity> save(List<DishEntity> dishes);

    List<DishEntity> getDishes(List<Long> dishIds);

    Map<Long,DishEntity> getDictionaryForIdList(List<Long> dishIds);

    List<TagEntity> getDishesForTagChildren(Long tagId, String name);

    void updateLastAddedForDish(Long dishId);
}
