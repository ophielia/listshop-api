package com.meg.atable.service.impl;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DishEntity> getDishesForUserId(Long userId) {
        UserAccountEntity user = this.userRepository.findOne(userId);
        return dishRepository.findByUserAccount(user);
    }

    @Override
    public Collection<DishEntity> getDishesForUserName(String userName) {
        return dishRepository.findByUserAccountUsername(userName);
    }

    @Override
    public Optional<DishEntity> getDishById(Long dishId) {
        return Optional.of(dishRepository.findOne(dishId));
    }

    @Override
    public DishEntity save(DishEntity dish) {
        return dishRepository.save(dish);
    }

    @Override
    public void deleteAll() {
        dishRepository.deleteAllInBatch();
    }


}
