package com.meg.atable.lmt.data.repository;


import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<TagEntity, Long> {


    List<TagEntity> findTagsByDishes(DishEntity dish);

    List<TagEntity> findTagsByTagTypeInOrderByName(List<TagType> tagTypes);


    List<TagEntity> findTagsByTagTypeAndTagTypeDefault(TagType tagType, boolean isDefault);

    List<TagEntity> findTagsBySearchSelectAndTagTypeIsIn(Boolean searchSelect, List<TagType> tagtypes);

    List<TagEntity> findTagsBySearchSelect(Boolean searchSelect);

    List<TagEntity> findTagsByAssignSelectAndTagTypeIsIn(Boolean assignSelect, List<TagType> tagtypes);

    List<TagEntity> findTagsByAssignSelect(Boolean assignSelect);

    List<TagEntity> findTagsByToDeleteTrue();

    List<TagEntity> findTagsByIsDisplay(Boolean isDisplay);

    @Query(value = "select t.* from dish_tags dt, " +
            "tag t where t.tag_id = dt.tag_id and  t.tag_type = 'Ingredient' and dt.dish_id in (:dishIdList) ", nativeQuery = true)
    List<TagEntity> getIngredientTagsForDishes(@Param("dishIdList") List<Long> dishIdList);

    @Query(value = "select t.*  from tag t " +
            "left outer join category_tags ct on ct.tag_id = t.tag_id and ct.category_id in " +
            "   (select category_id from list_category" +
            "   where layout_id = :layoutId) " +
            "where ct.tag_id is null and t.tag_type in ('Ingredient', 'NonEdible') and t.assign_select = true " +
            "order by t.name;", nativeQuery = true)
    List<TagEntity> getUncategorizedTagsForList(@Param("layoutId") Long listLayoutId);

    @Query(value = "select t.*  from tag t join category_tags ct on ct.tag_id = t.tag_id and ct.category_id = :layoutCategoryId order by t.name ", nativeQuery = true)
    List<TagEntity> getTagsForLayoutCategory(@Param("layoutCategoryId") Long layoutCategoryId);

    @Query(value = "select distinct t.* from tag t " +
            "join tag_relation tr on tr.parent_tag_id = t.tag_id " +
            "and t.tag_type in (:tagTypeList) ;", nativeQuery = true)
    List<TagEntity> findParentTagsByTagTypes(@Param("tagTypeList") List<String> tagTypeList);

    @Query(value = "select distinct t.* from tag t " +
            "join tag_relation tr on tr.parent_tag_id = t.tag_id;", nativeQuery = true)
    List<TagEntity> findParentTags();

    @Query("select distinct t.tagId FROM TagEntity t , DishEntity d " +
            "where d member of t.dishes and d.dish_id = ?1")
    Set<Long> getTagIdsForDish(Long dishId);

}