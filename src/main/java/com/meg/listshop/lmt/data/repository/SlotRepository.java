package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.data.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface SlotRepository extends JpaRepository<SlotEntity, Long> {
    List<SlotEntity> findByMealPlan(MealPlanEntity mealPlanEntity);
}
