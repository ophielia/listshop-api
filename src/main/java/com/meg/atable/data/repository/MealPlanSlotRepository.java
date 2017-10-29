package com.meg.atable.data.repository;

import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface MealPlanSlotRepository extends JpaRepository<SlotEntity, Long> {
    List<SlotEntity> findByMealPlan(MealPlanEntity mealPlanEntity);
}
