package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<TagEntity, Long>, CustomTagRepository {


    List<TagEntity> findTagsByDishes(DishEntity dish);

    List<TagEntity> findTagsByTagTypeAndTagTypeDefault(TagType tagType, boolean isDefault);

    @Query(value = "select t.* FROM tag t " +
            "where t.tag_id in (:tagIds) and t.replacement_tag_id is not null",
            nativeQuery = true)
    List<TagEntity> findTagsToBeReplaced(@Param("tagIds") Set<Long> tagIds);


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

    @Query("select distinct t.tag_id FROM TagEntity t , DishEntity d " +
            "where d member of t.dishes and d.dish_id = ?1")
    Set<Long> getTagIdsForDish(Long dishId);


    @Query(value = "    select t.* " +
            "    from tag t " +
            "    join tag_relation tr on tr.child_tag_id = t.tag_id " +
            "    join dish_tags dt on dt.tag_id = t.tag_id " +
            "    where tr.parent_tag_id = ?2 " +
            "    and dish_id = ?1", nativeQuery = true)
    TagEntity getAssignedTagForRating(Long dishId, Long ratingId);



    @Query(value = "SELECT t.*  " +
            "FROM   tag t  " +
            "       JOIN tag_relation tr  " +
            "         ON tr.child_tag_id = t.tag_id  " +
            "WHERE  tr.parent_tag_id = ?1  " +
            "       AND t.power > (SELECT power  " +
            "                      FROM   tag  " +
            "                      WHERE  tag_id = ?2)  " +
            "ORDER  BY power   " +
            "LIMIT  1 ", nativeQuery = true)
    TagEntity getNextRatingUp(Long parentRatingId, Long currentId);

    @Query(value = "SELECT t.*  " +
            "FROM   tag t  " +
            "       JOIN tag_relation tr  " +
            "         ON tr.child_tag_id = t.tag_id  " +
            "WHERE  tr.parent_tag_id = ?1  " +
            "       AND t.power < (SELECT power  " +
            "                      FROM   tag  " +
            "                      WHERE  tag_id = ?2)  " +
            "ORDER  BY power DESC  " +
            "LIMIT  1 ", nativeQuery = true)
    TagEntity getNextRatingDown(Long parentRatingId, Long currentId);

    @EntityGraph(value = "tag-category-graph")
    @Query(value = "select distinct t FROM TagEntity t , ListLayoutCategoryEntity c " +
            "            where c member of t.categories and c.layoutId = ?2 " +
            "            and t.categoryUpdatedOn > ?1 ")
    List<TagEntity> getTagsWithCategoriesChangedAfter(Date changedAfter, Long listLayoutId);

    @Query(value = "select t from TagEntity t where t.tag_id in (:tagIds)")
    List<TagEntity> getTagsForIdList(@Param("tagIds") Set<Long> tagIds);

    @Modifying
    @Query("update TagEntity t set t.userId = :userId where t.userId is null and t.tag_id in (:tagIds)")
    void assignTagsToUser(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    @Modifying
    @Query("update TagEntity t set t.isVerified = true where t.tag_id in (:tagIds)")
    void setTagsAsVerified(@Param("tagIds") List<Long> tagIds);
}