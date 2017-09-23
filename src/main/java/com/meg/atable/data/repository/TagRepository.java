package com.meg.atable.data.repository;


import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity,Long> {

/*
    @Query(value = "SELECT t FROM Tag t where t.dishes in :dish")// where d.dishes = :dish"
    List<Tag> findTagsForDish(@Param("dish") Dish dish);
*/

    List<TagEntity> findTagsByDishes(DishEntity dish);

}