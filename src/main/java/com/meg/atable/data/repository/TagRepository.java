package com.meg.atable.data.repository;


import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

/*
    @Query(value = "SELECT t FROM TagType t where t.dishes in :dish")// where d.dishes = :dish"
    List<TagType> findTagsForDish(@Param("dish") Dish dish);
*/

    List<TagEntity> findTagsByDishes(DishEntity dish);

    List<TagEntity> findTagsByTagTypeOrderByName(TagType tagType);


    List<TagEntity> findTagsByTagTypeAndTagTypeDefault(TagType tagType, boolean isDefault);


    @Query(value = "select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id", nativeQuery = true)
    List<TagEntity> findTagsWithoutChildren();

    @Query(value="select t.* from dish_tags dt, " +
            "tag t where t.tag_id = dt.tag_id and  t.tag_type = 'Ingredient' and dt.dish_id in (:dishIdList) ", nativeQuery=true)
    List<TagEntity> getIngredientTagsForDishes(@Param("dishIdList") List<Long> dishIdList);
}