package com.meg.atable.service.impl;

import com.meg.atable.model.Dish;
import com.meg.atable.model.User;
import com.meg.atable.repository.DishRepository;
import com.meg.atable.repository.UserRepository;
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
    public List<Dish> getDishesForUserId(Long userId) {
        User user = this.userRepository.findOne(userId);
        return dishRepository.findByUser(user);
    }

    @Override
    public Collection<Dish> getDishesForUserName(String userName) {
        return dishRepository.findByUserUserName(userName);
    }

    @Override
    public Optional<Dish> getDishById(Long dishId) {
        return Optional.of(dishRepository.findOne(dishId));
    }

    @Override
    public Dish save(Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    public void deleteAll() {
        dishRepository.deleteAllInBatch();
    }

    
}
