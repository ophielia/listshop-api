package com.meg.atable.repository;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

/*
    @Query(value = "SELECT t FROM Tag t where t.dishes in :dish")// where d.dishes = :dish"
    List<Tag> findTagsForDish(@Param("dish") Dish dish);
*/

    List<Tag> findTagsByDishes(Dish dish);

}