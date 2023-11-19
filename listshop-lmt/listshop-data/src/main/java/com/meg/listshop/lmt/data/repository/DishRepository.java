package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DishRepository extends JpaRepository<DishEntity, Long> {


    List<DishEntity> findByUserId(Long userid);

    @EntityGraph("filledDish")
    @Query("select d FROM DishEntity d where d.userId = ?1 and d.dish_id in (?2)")
    List<DishEntity> findByDishIdsForUser(Long userId, List<Long> dishIds);

    @EntityGraph("filledDish")
    @Query("select d FROM DishEntity d where d.dish_id = ?2 and d.userId = ?1")
    Optional<DishEntity> findByDishIdForUser(Long userId, Long dishId);


    @Query("select d FROM DishEntity d where (d.autoTagStatus < ?1 OR d.autoTagStatus is null)")
    List<DishEntity> findDishesToAutotag(Long statusFlag, Pageable pageable);

    @EntityGraph("filledDish")
    @Query("select d FROM DishEntity d where d.userId = ?1 and lower(trim(d.dishName)) like ?2 ")
    List<DishEntity> findByUserIdAndDishNameLike(Long userid, String name);

    @EntityGraph("filledDish")
    @Query("select d FROM DishEntity d where d.userId = ?1 and lower(trim(d.dishName)) = ?2 ")
    List<DishEntity> findByUserIdAndDishName(Long userid, String name);

}