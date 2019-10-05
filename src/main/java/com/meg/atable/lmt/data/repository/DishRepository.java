package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.DishEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByUserId(Long userid);

    @Query("select d FROM DishEntity d where d.userId = ?1 and d.id in (?2)")
    List<DishEntity> findByDishIdsForUser(Long user_id, List<Long> dish_ids);

    @Query("select d FROM DishEntity d where (d.autoTagStatus < ?1 OR d.autoTagStatus is null)")
    List<DishEntity> findDishesToAutotag(Long statusFlag, Pageable pageable);
}