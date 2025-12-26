package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.DishItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DishItemRepository extends JpaRepository<DishItemEntity, Long>, CustomDishItemRepository {

    @Query("select i FROM DishItemEntity i where i.dish.userId = ?1 and i.dish.dish_id in (?2) ")
    List<DishItemEntity> findByDishAndUser(Long userId, List<Long> dishIds);
}
