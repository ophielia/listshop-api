package com.meg.atable.data.repository;


import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {


    List<TagEntity> findTagsByDishes(DishEntity dish);

    List<TagEntity> findTagsByTagTypeInOrderByName(List<TagType> tagTypes);


    List<TagEntity> findTagsByTagTypeAndTagTypeDefault(TagType tagType, boolean isDefault);


    @Query(value = "select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id", nativeQuery = true)
    List<TagEntity> findTagsWithoutChildren();

    @Query(value = "select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id and ct.tag_type in (:tagtypes)", nativeQuery = true)
    List<TagEntity> findTagsWithoutChildrenByTagTypes(@Param("tagtypes") List<String> tagtypes);

    @Query(value = "select t.* from dish_tags dt, " +
            "tag t where t.tag_id = dt.tag_id and  t.tag_type = 'Ingredient' and dt.dish_id in (:dishIdList) ", nativeQuery = true)
    List<TagEntity> getIngredientTagsForDishes(@Param("dishIdList") List<Long> dishIdList);

    @Query(value = "select t.*  from tag t left outer join category_tags ct on ct.tag_id = t.tag_id and ct.category_id in (select category_id from list_category where layout_id = :layoutId) where ct.tag_id is null;", nativeQuery = true)
    List<TagEntity> getUncategorizedTagsForList(@Param("layoutId") Long listLayoutId);

    @Query(value = "select t.*  from tag t join category_tags ct on ct.tag_id = t.tag_id and ct.category_id = :layoutCategoryId", nativeQuery = true)
    List<TagEntity> getTagsForLayoutCategory(@Param("layoutCategoryId") Long layoutCategoryId);
}