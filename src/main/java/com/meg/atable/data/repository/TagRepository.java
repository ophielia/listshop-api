package com.meg.atable.data.repository;


import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {


    List<TagEntity> findTagsByDishes(DishEntity dish);

    List<TagEntity> findTagsByTagTypeInOrderByName(List<TagType> tagTypes);


    List<TagEntity> findTagsByTagTypeAndTagTypeDefault(TagType tagType, boolean isDefault);


    @Query(value = "select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id", nativeQuery = true)
    List<TagEntity> findTagsWithoutChildren();

    @Query(value = "select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id and ct.tag_type in (:tagtypes)", nativeQuery = true)
    List<TagEntity> findTagsWithoutChildrenByTagTypeIsIn(@Param("tagtypes") List<String> tagtypes);

    List<TagEntity> findTagsBySearchSelectAndTagTypeIsIn(Boolean searchSelect, List<String> tagtypes);

    List<TagEntity> findTagsBySearchSelect(Boolean searchSelect);

    @Query(value = "select t.* from dish_tags dt, " +
            "tag t where t.tag_id = dt.tag_id and  t.tag_type = 'Ingredient' and dt.dish_id in (:dishIdList) ", nativeQuery = true)
    List<TagEntity> getIngredientTagsForDishes(@Param("dishIdList") List<Long> dishIdList);

    @Query(value = "select t.*  from tag t join selectabletags s on s.child_tag_id = t.tag_id left outer join category_tags ct on ct.tag_id = t.tag_id and ct.category_id in (select category_id from list_category where layout_id = :layoutId) " +
            "where ct.tag_id is null and t.tag_type in ('Ingredient', 'NonEdible') order by t.name;", nativeQuery = true)
    List<TagEntity> getUncategorizedTagsForList(@Param("layoutId") Long listLayoutId);

    @Query(value = "select t.*  from tag t join category_tags ct on ct.tag_id = t.tag_id and ct.category_id = :layoutCategoryId", nativeQuery = true)
    List<TagEntity> getTagsForLayoutCategory(@Param("layoutCategoryId") Long layoutCategoryId);

    @Query(value="select distinct t.* from tag t " +
            "join tag_relation tr on tr.parent_tag_id = t.tag_id " +
            "and t.tag_type in (:tagTypeList) ;", nativeQuery=true)
    List<TagEntity> findParentTagsByTagTypes(@Param("tagTypeList") List<String> tagTypeList);

    @Query(value="select distinct t.* from tag t " +
            "join tag_relation tr on tr.parent_tag_id = t.tag_id;", nativeQuery=true)
    List<TagEntity> findParentTags();

    @Query("select t.tag_id, l.name FROM ListLayoutCategoryEntity  l, TagEntity t where t MEMBER OF l.tags and l.layoutId = ?1 and t in (?2)")
    List<Tuple> getTagCategoryKey(Long listLayoutId, List<TagEntity> tagEntities);

    @Query("select distinct t.autoTagFlag FROM TagEntity t , DishEntity d " +
            "where d member of t.dishes and d.dish_id = ?1")
    List<Integer> getAutoTagsForDish(Long dishId);
}