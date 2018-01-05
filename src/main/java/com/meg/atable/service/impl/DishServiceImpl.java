package com.meg.atable.service.impl;

import com.meg.atable.api.DishNotFoundException;
import com.meg.atable.api.UnauthorizedAccessException;
import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.service.AutoTagService;
import com.meg.atable.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AutoTagService autoTagService;


    @Override
    public Collection<DishEntity> getDishesForUserName(String userName) throws UserNotFoundException {
        UserAccountEntity user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return dishRepository.findByUserId(user.getId());
    }

    @Override
    public Optional<DishEntity> getDishById(Long dishId) {
        return Optional.of(dishRepository.findOne(dishId));
    }


    @Override
    public Optional<DishEntity> getDishForUserById(String username, Long dishId) {
        UserAccountEntity user = userRepository.findByUsername(username);
        DishEntity dish = dishRepository.findOne(dishId);
        if (dish == null) {
            throw new DishNotFoundException(dishId);
        }
        if (dish.getUserId() != user.getId()) {
            throw new UnauthorizedAccessException("Dish [" + dishId + "] doesn't belong to user [" + username + "].");
        }
        return Optional.of(dish);
    }

    @Override
    public DishEntity save(DishEntity dish, boolean doAutotag) {
        // autotag dish
        if (doAutotag) {
            autoTagService.doAutoTag(dish, true);
        }
        return dishRepository.save(dish);
    }

    @Override
    public  List<DishEntity> save(List<DishEntity> dishes) {
return dishRepository.save(dishes);
    }
    @Override
    public List<DishEntity> getDishes(List<Long> dishIds) {
        return dishRepository.findAll(dishIds);
    }

    @Override
    public Map<Long, DishEntity> getDictionaryForIdList(List<Long> dishIds) {
        List<DishEntity> tags = dishRepository.findAll(dishIds);
        if (!tags.isEmpty()) {
            return  tags.stream().collect(Collectors.toMap(DishEntity::getId,
                    c -> c));

        }
        return new HashMap<Long,DishEntity>();
    }
}
