package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishService {

    List<DishEntity> getDishesForUserName(String userId) ;

    DishEntity getDishForUserById(String username, Long dishId) ;

    DishEntity save(DishEntity dish, boolean doAutotag);

    DishEntity create(DishEntity dish);

    List<DishEntity> save(List<DishEntity> dishes);

    List<DishEntity> getDishes(List<Long> dishIds);

    List<DishEntity> getDishes(String userName, List<Long> dishIds);

    Map<Long, DishEntity> getDictionaryForIdList(List<Long> dishIds);

    List<TagEntity> getDishesForTagChildren(Long tagId, String name);

    void updateLastAddedForDish(Long dishId);

    List<DishEntity> getDishesToAutotag(Long statusFlag, int dishLimit);
}
