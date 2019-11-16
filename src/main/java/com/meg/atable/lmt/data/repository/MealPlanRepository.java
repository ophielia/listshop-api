package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.MealPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {
    List<MealPlanEntity> findByUserIdOrderByCreated(Long id);


}
