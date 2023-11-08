package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {
    @EntityGraph("mealplan-dish-entity-graph")
    List<MealPlanEntity> findByUserIdOrderByCreated(Long id);


}
