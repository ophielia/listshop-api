package com.meg.atable.service;

import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.data.entity.DishEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishSearchService {

    List<DishEntity> findDishes(DishSearchCriteria criteria);
}
