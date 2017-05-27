package com.meg.atable.repository;

import com.meg.atable.model.Dish;
import com.meg.atable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DishRepository extends JpaRepository<Dish,Long> {

    List<Dish> findByUser(User user);

    Collection<Dish> findByUserUserName(String userName);
}