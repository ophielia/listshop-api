package com.meg.atable.repository;

import com.meg.atable.model.Dish;
import com.meg.atable.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DishRepository extends JpaRepository<Dish,Long> {

    List<Dish> findByUserAccount(UserAccount user);

    Collection<Dish> findByUserAccountUserName(String userName);
}