package com.meg.atable.repository;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import com.meg.atable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

/*
    @Query(value = "SELECT t FROM Tag t where t.dishes in :dish")// where d.dishes = :dish"
    List<Tag> findTagsForDish(@Param("dish") Dish dish);
*/

    List<Tag> findTagsByDishes(Dish dish);

}